package edu.stthomas.gps.multiplevalues;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class WritableMultipleValues extends Configured implements Tool {

	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJarByClass(WritableMultipleValues.class);
		job.setJobName("Writable Multiple Values");

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(WritableMultipleValuesMapper.class);
		job.setReducerClass(WritableMultipleValuesReducer.class);

		job.setNumReduceTasks(1);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MultipleWritable.class);

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new WritableMultipleValues(), args);
		System.exit(exitCode);
	}
}
