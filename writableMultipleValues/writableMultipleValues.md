# Passing Multiple Values in MapReduce Part 2: Custom Writables

Bradley S. Rubin, PhD 9/27/2013

### This part shows how to use a custom Writable to pass multiple values between mapper and reducer, and from the reducer to output
---
In this 2nd of the 4-part series, I will show how to use a single class, which can contain muliple attributes, to pass multiple values.  This is much more efficient than the previous string-based technique, both in terms of storage size and in development time, also avoiding type conversions.

In the code for this part, we create a separate class that implements the Writable interface and contains, as its attributes, the multiple values and their types.  We then use this writable just like any of the other builtin types.  The toString() method controls how the multiple values appear in the HDFS text file.

Two notes:  

1. It is important to implement the empty default constructor.  
2. If you want to use a custom writable in the MRUnit test framework, you must also implement hashCode() and equals() (let Eclipse generate the code for you).  

## The Custom Writable

	package edu.stthomas.gps.multiplevalues;

	import java.io.DataInput;
	import java.io.DataOutput;
	import java.io.IOException;

	import org.apache.hadoop.io.Writable;

	public class MultipleWritable implements Writable {
	
	private int intField;
	private float floatField;
	private String stringField;

	public MultipleWritable() {
	}

	public MultipleWritable(int i, float f, String s) {
		intField = i;
		floatField = f;
		stringField = s;
	}

	public int getIntField() {
		return intField;
	}

	public void setIntField(int intField) {
		this.intField = intField;
	}

	public float getFloatField() {
		return floatField;
	}

	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public void readFields(DataInput in) throws IOException {
		intField = in.readInt();
		floatField = in.readFloat();
		stringField = in.readUTF();
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(intField);
		out.writeFloat(floatField);
		out.writeUTF(stringField);
	}

	@Override
	public String toString() {
		return intField + "\t" + floatField + "\t" + stringField;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + intField;
		result = prime * result + Float.floatToIntBits(floatField);
		result = prime * result
				+ ((stringField == null) ? 0 : stringField.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultipleWritable other = (MultipleWritable) obj;
		if (intField != other.intField)
			return false;
		if (Float.floatToIntBits(floatField) != Float
				.floatToIntBits(other.floatField))
			return false;
		if (stringField == null) {
			if (other.stringField != null)
				return false;
		} else if (!stringField.equals(other.stringField))
			return false;
		return true;
	}
	}

## The Mapper
	package edu.stthomas.gps.multiplevalues;

	import java.io.IOException;

	import org.apache.hadoop.io.LongWritable;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Mapper;

	/* An example of how to pass multiple values from a 	mapper to a reducer in a single custom writable value.
	Input is a comma-separated string, interpreted as Key:string Value:integer, float, string (i.e. "A,1,2.0,This is a test").
	Output is Key:string Value: MultipleWritable(integer, float, string), which contains the integer and float doubled
	(i.e. Key:"A" Value: 2 4.0 This is a test).
	*/

	public class WritableMultipleValuesMapper extends
		Mapper<LongWritable, Text, Text, MultipleWritable> {

	Text textKey = new Text();
	MultipleWritable mw = new MultipleWritable();

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
		if (field.length == 4) {
			mw.setIntField(Integer.parseInt(field[1]) * 2);
			mw.setFloatField(Float.parseFloat(field[2]) * 2);
			mw.setStringField(field[3]);

			textKey.set(field[0]);

			context.write(textKey, mw);
		}
	}
	}


## The Reducer
	package edu.stthomas.gps.multiplevalues;

	import java.io.IOException;

	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Reducer;

	/* An example of how to output multiple values from a reducer in a custom writable.
	Input is a Key:string Value:MultipleWritable(integer, float, string)
	(i.e. Key:"A" Value: MultipleWritable(2 4.0 This is a test).  Output is
	Key:string Value:MultipleWritable(integer, float, string) and the value contains
	the integer and float doubled in tab separated format in order to make future Hive/Pig import easier because
	keys and values are also separated by tabs. (i.e. Key:"A" Value: "4\t8.0\tThis is a test").
	*/

	public class WritableMultipleValuesReducer extends
		Reducer<Text, MultipleWritable, Text, MultipleWritable> {

	MultipleWritable mw = new MultipleWritable();

	@Override
	public void reduce(Text key, Iterable<MultipleWritable> values,
			Context context) throws IOException, InterruptedException {

		for (MultipleWritable value : values) {

			mw.setIntField(value.getIntField() * 2);
			mw.setFloatField(value.getFloatField() * 2);
			mw.setStringField(value.getStringField());

			context.write(key, mw);
		}
	}
	}


## The Driver
	package edu.stthomas.gps.multiplevalues;

	import org.apache.hadoop.conf.Configured;
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Job;
	import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
	import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
	import org.apache.hadoop.util.Tool;
	import org.apache.hadoop.util.ToolRunner;

	public class WritableMultipleValues extends Configured implements Tool {

	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJarByClass(WritableMultipleValues.class);
		job.setJobName("Writable Multiple Values");

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(WritableMultipleValuesMapper.class);
		job.setReducerClass(WritableMultipleValuesReducer.class);

		job.setNumReduceTasks(1);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MultipleWritable.class);

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new WritableMultipleValues(), args);
		System.exit(exitCode);
	}
	}
	
[You can get the three Java source files here](https://github.com/CoE4BD/HadoopHowTo/blob/master/writableMultipleValues/)



	