package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * An example of how to pass multiple values from a mapper to a reducer in a
 * single string value via string concatenation. Input is a comma-separated
 * string, interpreted as Key:string Value:integer, float, string (i.e.
 * "A,1,2.0,This is a test"). Output is Key:string Value:string, and the value
 * contains the integer and float doubled (i.e. Key:"A" Value:
 * "2,4.0,This is a test").
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
