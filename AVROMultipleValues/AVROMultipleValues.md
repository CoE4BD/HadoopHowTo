# Passing Multiple Values in MapReduce Part 4: AVRO

Brad Rubin  
10/2/2013

### This final part shows how to use AVRO to pass multiple values between mapper and reducer, and from the reducer to output.
---
In this final document of the 4-part series, I will show how to use a single AVRO class, which can contain muliple attributes, to pass multiple values.  This is much more efficient than the previously described string-based technique, also avoiding type conversions.  Since AVRO is self-describing and compatible with non-Java programming languages, it is a more versitile approach than using a Java custom writable.  However, that flexibility comes at the cost of a more complex development path.

In the code for this part, we create a separate AVRO class that contains, as its attributes, the multiple values and their types.  We then use this AVRO class just like any of the other builtin types.

Development with AVRO starts with a JSON-based schema, describing the contained types.  In this case, we are using an integer, a float, and a string as the three attributes that we want to treat as a single value.

## The AVRO Schema (Multiple.avsc)

	{"namespace": "edu.stthomas.gps.multiplevalues",
		"type": "record",
		"name": "Multiple",
		"fields": [
    	 	{"name": "intField", "type": "int"},
     		{"name": "floatField",  "type": "float"},
     		{"name": "stringField", "type": "string"}
		]
	}
	
This schema is them compiled into a .java file, in this example it is called Multiple.java.  This is most easily done with a Maven setup.  The Multiple.avsc source is assumed to reside in the resources directory, and the output is in the java source directory where the other .java files reside.

## Maven pom.xml

	<avro.version>1.7.3</avro.version>
  	<plugin>
    	<groupId>org.apache.avro</groupId>
    	<artifactId>avro-maven-plugin</artifactId>
    	<version>1.7.5</version>
    	<executions>
      	<execution>
        	<phase>generate-sources</phase>
        	<goals>
          		<goal>schema</goal>
        	</goals>
        	<configuration>
          		<sourceDirectory>${project.basedir}/src/main/resources</sourceDirectory>
          		<outputDirectory>${project.basedir}/src/main/java/</outputDirectory>
        	</configuration>
      	</execution>
   		</executions>
  	</plugin>
  	
  	<dependency>
    	<groupId>org.apache.avro</groupId>
    	<artifactId>avro</artifactId>
    	<version>${avro.version}</version>
  	</dependency>
  	<dependency>
    	<groupId>org.apache.avro</groupId>
    	<artifactId>avro-mapred</artifactId>
    	<version>${avro.version}</version>
    	<classifier>hadoop2</classifier>
  	</dependency>

## The Mapper

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

## The Reducer

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

## The Driver
	
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
	
## Viewing Your Output

After running your MapReduce jobs, you can't view your HDFS output file directly.  You must first get your file to the local filesystem and them run a tool to convert the AVRO binary into JSON.

	hadoop fs -get MultipleValues/AVRO/part-r-00000.avro  
	java -jar avro-tools-1.7.5.jar tojson part-r-00000.avro 

You can find the avro-tools JAR file [here](http://apache.org/dist/avro/avro-1.7.5/java/).

[You can get the four source files here](https://github.com/CoE4BD/HadoopHowTo/blob/master/AVROMultipleValues/).



	