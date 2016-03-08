## Extending the Spark API ##

Lawrence Kyei & Brad Rubin  
2/16/2016

Apache Spark has a lot of convienent built-in APIs that are similar to, but are more general than, the MapReduce APIs. For example, in MapReduce, the Context object gives access to a Counter API for accumulating statistics. This function has a parallel in Spark, called the Accumulator API.

Spark filter is the standard Spark function to filter out bad records. For example, the code below will throw out all records that only have 0 or 1 field.

    scala> val lines = sc.textFile("hdfs:///SEIS736/tweets").map(_.split("\t"))
 
    scala> lines.filter(_.length >= 2).take(1)
    
    res2: Array[Array[String]] = Array(Array(DarrenDalrymple, Are you a recent computer science graduate (or similar course) looking to start your career with a global software house? Get in touch ASAP, en, Thu Sep 26 08:25:42 CDT 2013, null))


But, in addition to throwing out bad records, it would be useful to keep a count of the bad records. Like counters in Java MapReduce, Spark has accumulators, we can do this.

    scala> val badRecords = sc.accumulator(0, "Bad Records")
   
    scala> val lines = sc.textFile("hdfs:///SEIS736/tweets").map(_.split("\t"))
    
    scala> lines.filter(x => if (x.size < 2) {badRecords += 1; false} else true).count
    res1: Long = 21396

    scala> badRecords.value
    res2: Int = 1643

From the above code, the accumulator value also shows up in the Spark GUI under the matching Stage. However this code is a little ugly, and in Scala there is a feature called implicits that can make the code cleaner. Implicits provide a way to seamlessly extend closed classes. For example, if one calls a method m on an object o of class C, and that class does not support method m, then Scala will look for an implicit conversion from C to something that does support m.

If we pay the one time price to put in this piece of code that extends the Spark API, with a method defined as clean():

    scala> import scala.reflect.ClassTag
    scala> import org.apache.spark.rdd.RDD                               
    scala> import org.apache.spark.Accumulator

     scala> implicit class CustomSparkFunctions[T: ClassTag](rdd: RDD[T]) {
          |        def clean(f: T => Boolean, a: Accumulator[Int]): RDD[T] = {
          |        rdd.filter(x => if (f(x)) true else {a += 1; false})
          |        }
          |     }

We get a much cleaner API cleaner interface that looks like it is part of the Spark API. It is also important to note that when running this code in spark-shell, we will have to import the RDD, Accumulator, and ClassTag types as they are not automatically imported.

    scala> val badRecords = sc.accumulator(0, "Bad Records")

    scala> val lines = sc.textFile("hdfs:///SEIS736/tweets").map(_.split("\t"))

    scala> lines.clean(_.size >= 2, badRecords).count
    res1: Long = 21396
    
    scala> badRecords.value
    res2: Int = 1643