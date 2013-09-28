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
