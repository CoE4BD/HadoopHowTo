package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * An example of how to pass multiple values from a mapper to a reducer in a
 * MapWritable with three entries (IntWritable, FloatWritable, and Text). Note
 * that the map's key must be a Writable too. The output is Key:string
 * Value:MapWritable and the integer and float are doubled (i.e. Key:"A"
 * Value:Map(1:2,2:4.0,3:"This is a test").
 */

public class MapMultipleValuesMapper extends
		Mapper<LongWritable, Text, Text, MapWritable> {

	Text textKey = new Text();
	MapWritable mw = new MapWritable();

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
		if (field.length == 4) {
			mw.put(new IntWritable(1),
					new IntWritable(Integer.parseInt(field[1]) * 2));
			mw.put(new IntWritable(2),
					new FloatWritable(Float.parseFloat(field[2]) * 2));
			mw.put(new IntWritable(3), new Text(field[3]));
			textKey.set(field[0]);

			context.write(textKey, mw);
		}
	}
}
