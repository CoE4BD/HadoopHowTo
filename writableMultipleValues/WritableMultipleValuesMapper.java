package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
/* An example of how to pass multiple values from a mapper to a reducer in a single custom writable value.
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
