package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AVROMultipleValuesMapper extends
		Mapper<LongWritable, Text, Text, AvroValue<Multiple>> {

	Text textKey = new Text();
	Multiple m = new Multiple();

	@Override
	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {

		String line = value.toString();
		String[] field = line.split(",");
		if (field.length == 4) {
			m.setIntField(Integer.valueOf(field[1]) * 2);
			m.setFloatField(Float.valueOf(field[2]) * 2);
			m.setStringField(field[3]);

			textKey.set(field[0]);
			AvroValue<Multiple> avm = new AvroValue<Multiple>(m);

			context.write(textKey, avm);
		}
	}
}

