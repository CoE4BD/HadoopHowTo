# Passing Multiple Values in Hadoop Part 1: Strings

Bradley S. Rubin, PhD 9/27/2013

### This document shows how to use a string to pass multiple values between mapper and reducer, and from the reducer to output
---
One of the first questions people have when learning the key/value pair model used in Hadoop is "How do I handle multiple values?"  This is the first of a 4-part series that will illustrate four techniques: String, Custom Writable, Map, and AVRO.

In the code for this part, we turn the values into strings and concatenate them together with comma separators to make a single string, and pass it in a Text object from mapper to reducer.  In the reducer, we can parse these multiple values and turn them back into their original types. We again do string concatenation to pass a single Text object to HDFS, but this time we use tab separators.  Since there is already a default tab separation convention between keys and values in the TextOutputFormat, using tabs in the values will make it easier for a subsequent Hive/Pig job to read both the keys and values and parse them, potentially also turning them into their original types.

## The mapper

	package edu.stthomas.gps.multiplevalues;

	import java.io.IOException;

	import org.apache.hadoop.io.FloatWritable;
	import org.apache.hadoop.io.LongWritable;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Mapper;

	/* An example of how to pass multiple values from a mapper to a reducer in a single string value via string concatenation.
	Input is a comma-separated string, interpreted as Key:string Value:integer, float, string (i.e. "A,1,2.0,This is a test").
	Output is Key:string Value:string, and the value contains the int and float doubled (i.e. Key:"A" Value: "2,4.0,This is a test").
	*/
	
	public class StringMultipleValuesMapper extends
		Mapper<LongWritable, Text, Text, Text> {

	Text textKey = new Text();
	Text textValue = new Text();
	FloatWritable floatWritable = new FloatWritable();

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
		if (field.length == 4) {
			int i = Integer.parseInt(field[1]);
			float f = Float.parseFloat(field[2]);
			String s = field[3];

			String v = String.valueOf(2 * i) + "," + String.valueOf(2 * f)
					+ "," + s;

			textKey.set(field[0]);
			textValue.set(v);

			context.write(textKey, textValue);
		}
	}	
	}

## The Reducer

	package edu.stthomas.gps.multiplevalues;

	import java.io.IOException;

	import org.apache.hadoop.io.FloatWritable;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Reducer;

	/* An example of how to output multiple values from a reducer in a single string value via string concatenation.
	Input is a comma-separated string, interpreted as Key:string Value:integer, float, string
	(i.e. Key:"A" Value:"2,4.0,This is a test").  Output is Key:string Value:string, and the value contains
	the int and float doubled in tab separated format in order to make future Hive/Pig import easier because
	keys and values are also separated by tabs. (i.e. Key:"A" Value: "4\t8.0\tThis is a test").
	*/
	public class StringMultipleValuesReducer extends
		Reducer<Text, Text, Text, Text> {

	Text textValue = new Text();
	FloatWritable floatWritable = new FloatWritable();

	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		for (Text value : values) {

			String line = value.toString();
			String[] field = line.split(",");

			int i = Integer.parseInt(field[0]);
			float f = Float.parseFloat(field[1]);
			String s = field[2];

			String v = String.valueOf(i * 2) + "\t" + String.valueOf(f * 2)
					+ "\t" + s;

			textValue.set(v);

			context.write(key, textValue);
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
	
	public class StringMultipleValues extends Configured implements Tool {

	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJarByClass(StringMultipleValues.class);
		job.setJobName("String Multiple Values");

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(StringMultipleValuesMapper.class);
		job.setReducerClass(StringMultipleValuesReducer.class);
		job.setNumReduceTasks(1);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new StringMultipleValues(), args);
		System.exit(exitCode);
	}	
	}



[You can get the three Java source files here](https://github.com/CoE4BD/HadoopHowTo/blob/master/stringMultipleValues/)
