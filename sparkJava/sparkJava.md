# Spark Java Wordcount

Brad Rubin  
6/19/2014

### Here is the classic wordcount example, using the Java API on Spark
---

###Notes: 

* Spark can run in three modes (standalone, YARN client, and YARN cluster).  This example uses the YARN cluster node, so jobs appear in the YARN application list (port 8088)
* The number of output files is controlled by the 4th command line argument, in this case it is 64.
* The input and output files (the 2nd and 3rd command line arguments) are HDFS files.
* This example as tested on CDH5.0, with Spark 0.9

## JavaWordCount.java

    package edu.stthomas.gps.spark;

	import java.util.Arrays;

	import org.apache.spark.api.java.JavaPairRDD;
	import org.apache.spark.api.java.JavaRDD;
	import org.apache.spark.api.java.JavaSparkContext;
	import org.apache.spark.api.java.function.*;

	import scala.Tuple2;

	@SuppressWarnings("serial")
	public final class JavaWordCount {

		public static void main(String[] args) throws Exception {
			if (args.length < 4) {
				System.err.println("Usage: JavaWordCount <master> <input> <output> <numOutputFiles>");
			System.exit(1);
			}

			JavaSparkContext spark = new JavaSparkContext(args[0],
				"Java Wordcount", System.getenv("SPARK_HOME"),
				JavaSparkContext.jarOfClass(JavaWordCount.class));

			JavaRDD<String> file = spark.textFile(args[1]);

			Integer numOutputFiles = Integer.parseInt(args[3]);

			JavaRDD<String> words = file
				.flatMap(new FlatMapFunction<String, String>() {
					public Iterable<String> call(String s) {
						return Arrays.asList(s.toLowerCase().split("\\W+"));
					}
				});

		JavaPairRDD<String, Integer> pairs = words
				.map(new PairFunction<String, String, Integer>() {
					public Tuple2<String, Integer> call(String s) {
						return new Tuple2<String, Integer>(s, 1);
					}
				});

		JavaPairRDD<String, Integer> counts = pairs.reduceByKey(
				new Function2<Integer, Integer, Integer>() {
					public Integer call(Integer a, Integer b) {
						return a + b;
					}
				}, numOutputFiles);

		counts.sortByKey(true).saveAsTextFile(args[2]);
		System.exit(0);
		}
	}


## Running

	source /etc/spark/conf/spark-env.sh
	export JAVA_HOME=/usr/java/jdk1.7.0_45-cloudera
	export SPARK_JAR=hdfs://hc.gps.stthomas.edu:8020/user/spark/share/lib/spark-assembly.jar
	hadoop fs -rm -r sparkout
	$SPARK_HOME/bin/spark-class org.apache.spark.deploy.yarn.Client \
      --jar ./Spark-1.0.jar \
      --class edu.stthomas.gps.spark.JavaWordCount \
      --args yarn-standalone \
      --args /SEIS736/gutenberg/* \
      --args sparkout \
      --args 64 \
      --num-workers 128 \
      --worker-memory 512M