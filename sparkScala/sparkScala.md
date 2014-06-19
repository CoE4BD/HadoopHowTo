# Spark Scala Wordcount

Brad Rubin  
6/19/2014

### Here is the classic wordcount example, using the Scala API on Spark
---

###Notes: 

* Spark can run in three modes (standalone, YARN client, and YARN cluster).  This example uses the YARN cluster node, so jobs appear in the YARN application list (port 8088)
* The number of output files is controlled by the 4th command line argument, in this case it is 64.
* The input and output files (the 2nd and 3rd command line arguments) are HDFS files.
* This example as tested on CDH5.0, with Spark 0.9

## ScalaWordCount.java

    package edu.stthomas.gps.spark;

	import org.apache.spark.SparkContext
	import org.apache.spark.SparkContext._

	object ScalaWordCount {
  		def main(args: Array[String]) {

   			val spark = new SparkContext(args(0), "Scala Wordcount", System.getenv("SPARK_HOME"), SparkContext.jarOfClass(this.getClass))

    		val numOutputFiles = args(3).toInt

    		if (args.length < 4) {
      			System.err.println("Usage: ScalaWordCount <master> <input>  
					<output> <numOutputFiles>");
      			System.exit(1);
    		}

    		val file = spark.textFile(args(1))
    		val counts = file.flatMap(line => line.split(" ")).  
				map(word => (word, 1)).reduceByKey(_ + _, numOutputFiles)
    		counts.sortByKey(true).saveAsTextFile(args(2))
    
    		System.exit(0)
  		}
	}

## Running

	source /etc/spark/conf/spark-env.sh
	export JAVA_HOME=/usr/java/jdk1.7.0_45-cloudera
	export SPARK_JAR=hdfs://hc.gps.stthomas.edu:8020/user/spark/share/lib/spark-assembly.jar
	hadoop fs -rm -r sparkout
	$SPARK_HOME/bin/spark-class org.apache.spark.deploy.yarn.Client \
      --jar ./Spark-1.0.jar \
      --class edu.stthomas.gps.spark.ScalaWordCount \
      --args yarn-standalone \
      --args /SEIS736/gutenberg/* \
      --args sparkout \
      --args 64 \
      --num-workers 128 \
      --worker-memory 512M