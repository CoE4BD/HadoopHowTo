package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * An example of how to output multiple values from a reducer in MapWritable.
 * Input is Key:string Value:MapWritable (i.e. Key:"A"
 * Value:Map(1:2,2:4.0,3:"This is a test"). The Output is the same, with the
 * integer and float doubled. Note that there is no toString() method in
 * MapWritable, so the values are not viewable in the output text file.
 */

public class MapMultipleValuesReducer extends
		Reducer<Text, MapWritable, Text, MapWritable> {

	MapWritable mw = new MapWritable();

	@Override
	public void reduce(Text key, Iterable<MapWritable> values, Context context)
			throws IOException, InterruptedException {

		for (MapWritable value : values) {

			int i = ((IntWritable) value.get(new IntWritable(1))).get();
			float f = ((FloatWritable) value.get(new IntWritable(2))).get();
			String s = ((Text) value.get(new IntWritable(3))).toString();

			mw.put(new IntWritable(1), new IntWritable(i * 2));
			mw.put(new IntWritable(2), new FloatWritable(f * 2));
			mw.put(new IntWritable(3), new Text(s));

			context.write(key, mw);
		}
	}
}
