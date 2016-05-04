## sbt (Simple Build Tool) ##

Mohammad Khan & Lawrence Kyei  
2/29/2016

This tutorial provides a quick path to compile, test and run a Scala application using sbt (simple build tool). sbt is an open source interactive build tool. 

### Downloading sbt ###

- Download the sbt zip file [here](http://www.scala-sbt.org/download.html)
- Reference the sbt tutorial [here](http://www.scala-sbt.org/0.13/tutorial/index.html)

### Example: Installing sbt manually on Linux ###
We will install sbt on UNIX manually. For a larger reference, click [here](http://www.scala-sbt.org/release/tutorial/Manual-Installation.html).

- Create a ~/bin directory.
- Move the sbt-launch.jar from ~/Downloads/sbt/bin to ~/bin.
- Create a script to run the sbt-launch.jar file, by creating ~/bin/sbt with these contents:

        #!/bin/bash
        SBT_OPTS="-Xms512M -Xmx1536M -Xss1M -XX:+CMSClassUnloadingEnabled -  XX:MaxPermSize=256M"
        java $SBT_OPTS -jar `dirname $0`/sbt-launch.jar "$@"
- Make the script executable:

        $ chmod u+x ~/bin

- Open the command line and run sbt

        $ sbt

When you run this sbt prompt shell appears, we can run a test program

    > test
    [info] Updating {file:/home/training/bin/}bin...
    [info] Resolving org.fusesource.jansi#jansi;1.4 ...
    [info] Done updating.
    [success] Total time: 3 s, completed Feb 29, 2016 10:06:01 PM
At this point your project directory is /home/training/bin.

### sbt Global Plugins ###
Create "plugins" directory under ~/.sbt/0.13/. It is important to do this from the command line as the .sbt directory is likely to be hidden after creating it hence preventing you from creating the sub directories.

Open the command line and create the following directories with these commands

    $ mkdir .sbt
    $ cd .sbt
    $ mkdir 0.13
    $ cd 0.13
    $ mkdir plugins

### Installing Sbteclipse Plugin:###
Under ~/.sbt/0.13/plugins create plugins.sbt if it is not already there. All plugin definitions go in ~/.sbt/0.13/plugins/plugins.sbt. Add this line to plugins.sbt:
 
    addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")

Execute the following commands

    $ > plugins.sbt
    $ echo "addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")" > plugins.sbt
    
We want to make sure plugins.sbt holds the text we want so we do:

    $ cat plugins.sbt
    addSbtPlugin(com.typesafe.sbteclipse % sbteclipse-plugin % 2.5.0)
Notice we lost our double quotes, we will have to fix it else we get `')' expected but double literal found` error. This is how to fix it from the command line, navigate into the plugins folder and execute the following commands

    vi plugins.sbt
    i       //to get into insert mode where you can edit
    esc     //to escape out of insert mode
    :wq     //to save and quit 

### Creating Project Using Sbteclipse ###
Under ~/workspace create a directory called "daveWordCount" (daveWordCount is your test project).

build.sbt contains a list of libraries that you need to download for your project.
Create a file called "build.sbt" under ~/workspace/daveWordCount with the following content:
  
    name := "Dave Word Count"
    version := "1.0"
    //To find scala version in Eclipse IDE:
    //Eclipse > Help > About Scala IDE > Installation Details > Plugins > Type "scala" in search field, look for version in "Scala Compiler"
    //But remember, Apache Spark 1.5.2 works with Scala 2.10 so far, not 2.11. You might have to download Apache Spark 1.6 for Scala 2.10 to work
    scalaVersion := "2.10.6"
    //For individual library dependencies, you can do this:
    //libraryDependencies += "org.apache.spark" %% "spark-core" % "1.0.0"
    //libraryDependencies += "org.apache.spark" %% "spark-sql" % "1.0.0"
    //For multiple library dependencies, and to exclude some of them, you can do this:
    libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.0.0",
    "org.apache.spark" %% "spark-sql" % "1.0.0" 
    ).map(_.exclude("org.slf4j", "slf4j=jdk14"))
    resolvers += "Akka Repository" at "http://repo.akka.io/releases/"
-In ~/workspace/daveWordCount folder type "sbt":

    $ sbt
    [info] Loading global plugins from /home/training/.sbt/0.13/plugins
    [info] Updating {file:/home/training/.sbt/0.13/plugins/}global-plugins...
    [info] Resolving org.scala-sbt.ivy#ivy;2.3.0-sbt-2cc8d2761242b072cedb0a04cb39435[info] Resolving org.fusesource.jansi#jansi;1.4 ...
    [info] downloading https://repo.typesafe.com/typesafe/ivy-releases/com.typesafe.sbteclipse/sbteclipse-plugin/scala_2.10/sbt_0.13/2.5.0/jars/sbteclipse-plugin.jar ...
When this is done, in "sbt interactive shell" prompt type "eclipse". The first time it will take a while to download all eclipse libraries:

    > eclipse
    [info] About to create Eclipse project files for your project(s).
    [info] Updating {file:/home/training/workspace/daveWordCount/}davewordcount...
    [info] Resolving org.fusesource.jansi#jansi;1.4 ...
### Importing Project into Eclipse ###
- In Eclipse, right click in the "Package Explorer"
- Click on "Import"
- Choose "General", "Existing Projects into Workspace"
- In "Import" window, select "Select root directory" radio button, and click on "Browse" button.
- Select your newly created folder "daveWordCount", and Click "OK" button.
- Back on "Import" window click "Finish" button.
- In Eclipse, under "Package Explorer", you will now see a folder called "daveWordCount"
- You might notice a red "X" on "daveWordCount". Those are problems
- Go to "Window", "Show View", "Other", type "Problems" in search box and select "Problems" view, it should be under General
- Under "Problems" view expand "Errors", multiple errors will say '... is cross-compiled with an incompatible version of Scala ...'

Error '... is cross-compiled with an incompatible version of Scala ...' can be ignored. See explanation below. 
Basically we just downloaded Scala 2.10 libraries, and Eclipse already had the same libraries under 2.11. 
However, Apache Spark 1.5.2 works with Scala 2.10 so far. You might have to download Apache Spark 1.6 for Scala 2.10 to work. 
But don't worry about that, we will build our Scala programs under Scala 2.10 libraries.

Source: [here](http://scala-ide.org/docs/current-user-doc/faq/index.html)

"Scala IDE complains about ‘... is cross-compiled with an incompatible version of Scala ...’
The Scala IDE tries to check if binary incompatible Scala libraries have been inadvertently mixed in a project’s classpath. 
It works by extracting, from the name of the jars, which major version of Scala it has been compiled 
against (assuming the Scala convention for publishing cross-compiled libraries, and further assuming that Scala minor releases are binary compatible). 
If the extracted Scala major version doesn’t match the one bundled with the Scala IDE, a problem is reported. 
This ad-hoc validation prevents one of the most common reason for compiler crashes and lack of intelligent behavior in the Scala IDE.

If this check returns a false-negative, it can be disabled at the workspace level, or at the project level. 
The setting is withVersionClasspathValidator in the Scala → Compiler → Build Manager preference section."

### Creating A Scala Object ###

- In Eclipse, under "Package Explorer", expand "daveWordCount"
- Right click on "src/main/scala-2.10", "New", "Scala Object"
- Name it "daveWordCount", you can call it whatever you want, you don't have to use the same name as folder name. Click on "Finish" button
- A new object called "daveWordCount.scala" will appear under "src/main/scala-2.10". That's your program file.

### Writing A WordCount Scala Program ###
Copy and paste this code in "daveWordCount" file. This code is a sample from Proferssor Dave Rubin:

    import org.apache.spark.SparkConf
    import org.apache.spark.SparkContext
    import org.apache.spark.SparkContext._
    import org.apache.spark.rdd.RDD

    object daveWordCount {
    
      var sc: SparkContext = _
      def setSC(s: SparkContext): Unit = sc = s
      
      def input(inputFileName: String): RDD[String] = {
        val sparkConf = new SparkConf().setAppName("Spark WordCount")
        setSC(new SparkContext(sparkConf))
        sc.textFile(inputFileName)
      } 
    
      def process(rddIn: RDD[String], numTasks: Int): RDD[(String, Int)] = rddIn
        .flatMap(line => line.split(" "))
        .map(word => (word, 1))
        .reduceByKey(_ + _, numTasks)
        .sortByKey(true)
        
      def output(rdd: RDD[(String, Int)], outputFileName: String): Unit = rdd.saveAsTextFile(outputFileName)
    
      def main(args: Array[String]) {
        if (args.length < 3) {
          println("Usage: daveWordCount <input> <output> <numOutputFiles>");
          System.exit(1);
        }
       val inputArg0: (Unit => RDD[String]) = Unit => input(args(0))
       val processArg2: (RDD[String] => RDD[(String, Int)]) = process(_,     args(2).toInt)
       val outputArg1: (RDD[(String, Int)] => Unit) = output(_, args(1))
    (inputArg0 andThen processArg2 andThen outputArg1)()    
       System.exit(0)
     }
    }

### Creating Jar File Using sbt Package ###
- Unlick other Java projects, here you'll be creating a jar file using sbt
- Go to your project folder from the command line, "/home/training/workspace/daveWordCount", and type "sbt package".

        $ sbt package
        [info] Loading global plugins from /home/training/.sbt/0.13/plugins
        [info] Set current project to Dave Word Count (in build file:/home/training/workspace/daveWorkCount/)
        [info] Compiling 1 Scala source to /home/training/workspace/daveWorkCount/target/scala-2.10/classes...
        [info] 'compiler-interface' not yet compiled for Scala 2.10.6. Compiling...
        [info]   Compilation completed in 47.347 s
        [info] Packaging /home/training/workspace/daveWorkCount/target/scala-2.10/dave-word-count_2.10-1.0.jar ...
        [info] Done packaging.
        [success] Total time: 67 s, completed Mar 1, 2016 5:15:59 PM

The jar file is created in the directory ~/target/scala-2.10 under daveWordCount directory.

### Running Your Spark Scala IDE Built Code With Spark-Submit ###
- Create and store a sample directory, "daveWordCount" and a sample text file under the directory, "daveWordCountText.txt", in hdfs, like so:

        $ hadoop fs -cat daveWordCount/daveWordCountText.txt
        hello, is it me youre looking for

- Create a directory, "daveWordCountResults" to save results of your program 
- In Linux, create this script with the following contents:
/home/training/workspace/daveWordCount/run_daveWordCount.sh

This file is saved as /home/training/workspace/daveWordCount/run_daveWordCount.sh:

    hdfs dfs -rm -r daveWordCountResults
    spark-submit --class daveWordCount \
    /home/training/workspace/daveWordCount/target/scala-2.10/dave-word-count_2.10-1.0.jar \
    daveWordCount daveWordCountResults 1
- Give execute permissions to user and group on the script

        $ chmod ug+x run_daveWordCount.sh
- Now run the script to execute your scala program

        $ /home/training/workspace/daveWordCount/run_daveWordCount.sh
### View The Results Of WordCount Program ###
    $ hdfs dfs -cat daveWordCountResults/part-00000
    (for,1)
    (hello,,1)
    (is,1)
    (it,1)
    (looking,1)
    (me,1)
    (youre,1)
### Updating Third-Party Libraries ###
When you need to add/remove/update third-party libraries, change the "build.sbt" file. 
Then in the "sbt interactive shell", run commands "reload" and then "eclipse". 
At last, in Eclipse, right click the project in Package Explorer, and click Refresh from the context menu.

