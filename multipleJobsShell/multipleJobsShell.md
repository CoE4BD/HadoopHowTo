# Chaining and Managing Multiple MapReduce Jobs Part 1: Using a BASH Shell

Michael Prom & Brad Rubin
11/20/2013

### This first of a four-part series showing how to chain and manage multiple MapReduce Jobs.
---
While a single MapReduce job may be sufficient for certain tasks, there may be instances where 2 or more jobs will be required.  Instead of executing jobs manually, we can automate this process using various methods.  

This series will show you how to chain multiple jobs together using four techniques: a shell script, within the driver code itself, using the JobControl class, and with the Oozie workflow scheduler.


## The Two MapReduce Jobs
  
This is a basic WordCount problem which uses two MapReduce jobs. The first, is a standard WordCount problem. It simply outputs the word as the key and the count of the word as the value.  The second MapReduce job inverts the key and the value in the mapper. The output is a sorted list of word counts as the key and the associated word as a value.

This same concept can be applied to other situations as well, where pre or post processing is needed outside of the core MapReduce job, or to support iterative algorithms, such as the PageRank graph algorithm.

## The Shell Script

Using a shell script to manage the sequential execution of the jobs. 

We will use the bash shell since it the default in most Linux distributions. Other shells will work as well. The script contains pre-define commands to be executed. 

**Note:**  Managing jobs using the shell will only allow jobs to be run sequentially. If your specific need requires the jobs to be run in parallel see the future how-tos in this series.   

The first step requires you to create an executable script file. We called this "WordCountInvert.sh". (Ensure that the permissions are set correcting using "chmod"). The bash script contains the variables to define the JAR files, input directory (shakespeare), and the commands to verify the result of the MapReduce Jobs. This script will output the results into a log file.  

The script file should look like the one below. Ensure that the variables are set for your needs.  This shell script is heavily inspired by Don Miner and Adam Shook's book *MapReduce Design Paterns*[^1], which has a chapter on Metapatterns that describes several job chaining options.

**Note:** COMMON_TEMP serves as the output filename of the 1st job, which is also the input to the second job.   
The LOG_FILE will show the output of the two MapReduce jobs and any errors encountered.

    #!/bin/bash
    # This script will run two MapReduce jobs. 
    # Jar file definition
    MAPREDUCE_JAR_JOB1="WordCount.jar"
    MAPREDUCE_JAR_JOB2="WordCountInvert.jar"
    
    # Job class definition. There are two main classes in this job. WordCount and WordCountInvert
    MAIN_CLASS_JOB1="WordCount"
    MAIN_CLASS_JOB2="WordCountInvert"
    
    HADOOP="$( which hadoop )"
    
    # Definition of the input directory. The shakespeare directory contains multiple text files. 
    INPUT_DIRECTORY="shakespeare"
    COMMON_TEMP="TEMP_DIR"
    
    # This is the final job output directory.
    FINAL_OUTPUT="JOB_CHAIN_RESULTS"
    
    #Define the two commands to execute the jobs. 
    JOB_1_CMD="${HADOOP} jar ${MAPREDUCE_JAR_JOB1} ${MAIN_CLASS_JOB1} \
    ${INPUT_DIRECTORY} ${COMMON_TEMP}"
    
    JOB_2_CMD="${HADOOP} jar ${MAPREDUCE_JAR_JOB2} ${MAIN_CLASS_JOB2} \
    ${COMMON_TEMP} ${FINAL_OUTPUT}"
    
    # Define the command to cat the output of the directory.
    CAT_FINAL_OUTPUT_CMD="${HADOOP} fs -cat ${FINAL_OUTPUT}/part-*"
    
    # Define the cleanup command
    CLEANUP_CMD="${HADOOP} fs -rm -r ${COMMON_TEMP} ${FINAL_OUTPUT}"
    
    # Define the log file
    LOG_FILE="LOG_`date +%s`.txt"
    
    #This command will execute each job and determine status. 
    #Results will be displayed in the log file. 
    {
    # Cleanup Command
    ${CLEANUP_CMD}
    
    ${JOB_1_CMD}
    if [ $? -ne 0 ]
    then
    echo "ERROR FIRST JOB. SEE LOG."
    ${CLEANUP_CMD}
    exit $?
    fi
    echo ${JOB_2_CMD}
    ${JOB_2_CMD}
    if [  
    $? -ne 0 ]
    then
    echo "ERROR SECOND JOB. SEE LOG."
    ${CLEANUP_CMD}
    exit $?
    fi
    echo ${CAT_FINAL_OUTPUT_CMD}
    ${CAT_FINAL_OUTPUT_CMD}
    
    ${CLEANUP_CMD}
    exit 0
    } &> ${LOG_FILE}

## File Execution

You can simply execute the file using ./WordCountInvert.csh from the command line (ensure x is set in the permissions). You can place multiple echo commands within the file and view the log file when complete.


## Pros and Cons

As noted in above, you can scale this up to many jobs. Since it is done in the shell, no recompiling is needed. However, as previously noted, jobs run in this manner can only be run sequentially. As the number of jobs increase, more lines are needed in the script file. The following parts in this series will describe more efficient ways to managing large number of jobs. 

[^1]: Miner and Shook, _MapReduce Design Patterns: Building Effective Algorithms and Analytics for Hadoop and Other Systems,_ O'Reilly, 2013.