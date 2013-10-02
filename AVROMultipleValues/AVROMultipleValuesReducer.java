package edu.stthomas.gps.multiplevalues;

import java.io.IOException;

import org.apache.avro.mapred.AvroValue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AVROMultipleValuesReducer extends
		Reducer<Text, AvroValue<Multiple>, Text, AvroValue<Multiple>> {

	AvroValue<Multiple> mw = new AvroValue<Multiple>();

	@Override
	public void reduce(Text key, Iterable<AvroValue<Multiple>> values,
			Context context) throws IOException, InterruptedException {

		for (AvroValue<Multiple> value : values) {

			Multiple m = new Multiple();
			m.setIntField(value.datum().getIntField() * 2);
			m.setFloatField(value.datum().getFloatField() * 2);
			m.setStringField(value.datum().getStringField());

			AvroValue<Multiple> out = new AvroValue<Multiple>(m);

			context.write(key, out);
		}
	}
}

