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
