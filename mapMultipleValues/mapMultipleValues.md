# Passing Multiple Values in MapReduce Part 1: Maps

Bradley S. Rubin, PhD 9/27/2013

### This part shows how to use a MapWritable to pass multiple values between mapper and reducer, and from the reducer to output
---
In the code for this part, we place the multiple values into a MapWritable object, which can be passed between the mapper and reducer, and also output from the reducer.  MapWritable consumes some memory.  It requires a new itself, and a new for every key and value within the map.  In this example, and IntWritable is used for the map key.

MapWritable doesn't implement toString(), so it won't display nicely when using hadoop fs -cat on the text file output.  Also, it isn't compatible with Hive and Pig, but it can be consumed by a subsequent mapper.  Finally, althought it can be used with MRUnit, the test framework complains because it too best works with classes with toString() to help debug test mismatches.  Perhaps it would be good to subclass MapWritable and add a custom toString() method.

## The Mapper

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

## The Reducer

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

## The Driver

	public class MapMultipleValues extends Configured implements Tool {

	public int run(String[] args) throws Exception {

		Job job = new Job(getConf());
		job.setJarByClass(MapMultipleValues.class);
		job.setJobName("Map Multiple Values");

		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(MapMultipleValuesMapper.class);
		job.setReducerClass(MapMultipleValuesReducer.class);

		job.setNumReduceTasks(1);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(MapWritable.class);

		return (job.waitForCompletion(true) ? 0 : 1);
	}

	public static void main(String[] args) throws Exception {
		int exitCode = ToolRunner.run(new MapMultipleValues(), args);
		System.exit(exitCode);
	}
	}

[You can get the three Java source files here](https://github.com/CoE4BD/HadoopHowTo/blob/master/mapMultipleValues/)
