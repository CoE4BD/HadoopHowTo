# Display your MapReduce Output
Brad Rubin  
10/1/2013

### Hadoop MapReduce can write key/value output to HDFS in a variety of formats. Here is how to display them. 
---
MapReduce output to HDFS, at its simplest, consists of a file of key/value text pairs, separated by a tab.  If you use data compression, sequence files, custom Writables, or AVRO types, the commands to display your output can vary.  Here is a chart showing the options.

For example, assume your MapReduce job outputs in SequenceFileOutputFormat, is compressed, and uses a custom Writable type, packaged in CustomWritable.jar.  The output file resides in HDFS in a file named part-r-00000.  The command you would use to display it is

``
hadoop fs -libjars CustomWritable.jar -text part-r-00000
``


| | Standard | Custom Writable | AVRO |
| ------------ | ------------- | ------------ | ------------ |
| Default | hadoop fs -cat  | hadoop fs -cat | get a local copy of the HDFS file, then: java -jar avro-tools-1.7.5.jar tojson |
| Compressed |hadoop fs -text  | hadoop fs -text | get a local copy of the HDFS file, then: java -jar avro-tools-1.7.5.jar tojson |
| SequenceFile | hadoop fs -text  | hadoop fs -libjars CustomWritable.jar -text | N/A |
| Sequence File Compressed | hadoop fs -text | hadoop fs -libjars CustomWritable.jar -text | N/A |

You can find the avro-tools JAR file [here](http://apache.org/dist/avro/avro-1.7.5/java/).