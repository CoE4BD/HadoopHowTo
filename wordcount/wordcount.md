# Wordcount

Brad Rubin  
2/17/2014

### Here is the classic wordcount example, using the new Hadoop API
---

## WordMapper.java

    import java.io.IOException;

    import org.apache.hadoop.io.IntWritable;
    import org.apache.hadoop.io.LongWritable;
    import org.apache.hadoop.io.Text;
    import org.apache.hadoop.mapreduce.Mapper;

    public class WordMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		IntWritable intWritable = new IntWritable(1);
		Text text = new Text();

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			for (String word : line.split("\\W+")) {
				if (word.length() > 0) {
					text.set(word);
					context.write(text, intWritable);
		   		}
	    	}
      	}
    }

## SumReducer.java
	import java.io.IOException;

	import org.apache.hadoop.io.IntWritable;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Reducer;

	public class SumReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		IntWritable intWritable = new IntWritable();

		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

			int wordCount = 0;
			for (IntWritable value : values) {
				wordCount += value.get();
			}
			intWritable.set(wordCount);
			context.write(key, intWritable);
		}
	}
## WordCount.java

	import org.apache.hadoop.conf.Configured;
	import org.apache.hadoop.fs.Path;
	import org.apache.hadoop.io.IntWritable;
	import org.apache.hadoop.io.Text;
	import org.apache.hadoop.mapreduce.Job;
	import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
	import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
	import org.apache.hadoop.util.Tool;
	import org.apache.hadoop.util.ToolRunner;

	public class WordCount extends Configured implements Tool {

		public int run(String[] args) throws Exception {

			Job job = new Job(getConf());
			job.setJarByClass(WordCount.class);
			job.setJobName("Word Count");

			FileInputFormat.setInputPaths(job, new Path(args[0]));
			FileOutputFormat.setOutputPath(job, new Path(args[1]));

			job.setMapperClass(WordMapper.class);
			job.setReducerClass(SumReducer.class);
			job.setCombinerClass(SumReducer.class);
			//job.setNumReduceTasks(2);

			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			return (job.waitForCompletion(true) ? 0 : 1);
		}

		public static void main(String[] args) throws Exception {
			int exitCode = ToolRunner.run(new WordCount(), args);
			System.exit(exitCode);
		}
	}


## Compiling and Running

	hadoop fs -rm -r output	javac -cp `hadoop classpath` *.java	jar cvf WordCount.jar *.class	hadoop jar WordCount.jar WordCount -D mapred.reduce.tasks=2 input output	
[You can get the three Java source files here](https://github.com/CoE4BD/HadoopHowTo/blob/master/wordcount/)



	