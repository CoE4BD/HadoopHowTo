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
