package edu.stthomas.gps.multiplevalues;

import org.apache.avro.mapred.AvroValue;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyValueOutputFormat;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class AVROMultipleValues extends Configured implements Tool {

	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJarByClass(AVROMultipleValues.class);
		job.setJobName("AVRO Multiple Values");

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(AVROMultipleValuesMapper.class);
		job.setReducerClass(AVROMultipleValuesReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(AvroValue.class);

		job.setOutputFormatClass(AvroKeyValueOutputFormat.class);
		AvroJob.setMapOutputValueSchema(job, Multiple.SCHEMA$);
		AvroJob.setOutputValueSchema(job, Multiple.SCHEMA$);

		job.setNumReduceTasks(1);

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new AVROMultipleValues(), args);
		System.exit(exitCode);
	}
}

