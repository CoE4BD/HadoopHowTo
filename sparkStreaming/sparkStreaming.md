# Spark Streaming

Lawrence Kyei
4/26/2016

Spark Streaming is a distributed data stream processing framework. It is a Spark add-on that runs on top of Spark. This uses Spark Core under the hood to process streaming data, providing a scalable, fault-tolerant and high-throughput distributed stream processing platform.

For this example, we will simply demonstrate the streaming interface using data coming from an HDFS file, which isn't a typical use case. Getting streaming data from Twitter, IoT sensors, etc. is a more typical application of this technology.

###High-Level Architecture
Spark Streaming processes streaming data in micro-batches. It splits streaming data into batches of very small fixed-sized time intervals which are stored as an RDD. RDD operations like transformation and action can be applied to this RDD, which in turn stream out in batches.

### Basic Structure of a Spark Streaming Application
---  
The code below shows an outline of the Spark Streaming application with no processing logic yet. We will add snippets to this skeleton to get a complete working application as we move along.

	import org.apache.spark._
	import org.apache.spark.streaming._

	object BasicStreamApp{
		def main(args: Array[String]): Unit ={

			//define the batching interval
			val interval = args(0).toInt

			//Set the system properties
			val conf = new SparkConf()

			//Create an instance of the StreamingContext class and specify the batching interval or duration for each RDD
			val ssc = new StreamingContext(Conf, Seconds(Interval))

			// add your application specific and transformation logic here
			...
			...
			...
			
			//No processing starts until you call this method
			ssc.start()

			//waits for the stream computationt to stop
			ssc.awaitTermination()
		}
	}

###Working Spark Streaming Application
Now Let's try a simple working Spark Streaming Application. For simplicity this application will stream the word count from an HDFS data source to our Spark REPL.

**HDFS Streaming Source**

Let's make a directory in HDFS and move a text file into it that contains the words to be streamed and counted.

	$hadoop fs -mkdir /user/kyei/stream

Now we can move our .txt file from our location to the HDFS location

	$hadoop fs -put /home/kyei/wordcount.txt /user/kyei/stream

Next we can start the spark-shell

	$spark-shell

We paste our code

	//package edu.stthomas.gps.spark

	import org.apache.spark.SparkConf
	import org.apache.spark.streaming.{Seconds, StreamingContext}

	/**
  	* Counts words in text files created in hdfs directory
  	* Usage: StreamCount <directory>
  	*   <directory> is the directory that Spark Streaming will use to find and 	read text files.
  	*
  	*/
	object StreamCount {
	  def main(args: Array[String]) {
	    if (args.length < 1) {
	      System.err.println("Usage: StreamCount <directory>")
	      System.exit(1)
	    }

	    //Stream configuration
	    val sparkConf = new SparkConf().setAppName("StreamCount").setMaster("local[2]").set("spark.driver.allowMultipleContexts", "true")
	    // Create the StreamContext class
	    val ssc = new StreamingContext(sparkConf, Seconds(2))

	    // Create the FileInputDStream on the directory and use the
	    // stream to count words in new files created
	    val lines = ssc.textFileStream(args(0))
	    val words = lines.flatMap(_.split(" "))
	    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)
	    wordCounts.print()
	    ssc.start()
	    ssc.awaitTermination()
	  }
	}

###Running and Streaming Data

To run and have your data streaming in, we run the command

	  StreamCount.main(Array("hdfs:///user/kyei/stream/"))

	