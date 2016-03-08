## Hive Joins in MapReduce ##

Lawrence Kyei & Brad Rubin  
2/2/2016

Hive, like any other SQL language, allows users to join tables. However, joins can be computationally expensive, especially on large tables. Throughout the years, many join strategies have been added to Hive, some of which are:

- Common (Reduce-side) Join
- Broadcast (Map-side) Join
- Bucket Map Join

We illustrate the first and third join type below, using the following input data:

The following commands show the files left and right in the HDFS directory. We will join on the numeric key.

    $ hadoop fs -cat /user/brad/input/left/*
    
    1,A
    1,B
    4,A
    2,C
    5,D
    3,A
    3,B
    6,A

    $ hadoop fs -cat /user/brad/input/right/*
    
    1,X
    4,Y
    2,X
    5,Y
    5,Z
    3,X
    3,Z

Drop Table if they already exist

	DROP TABLE left;
    DROP TABLE right; 
    DROP TABLE cleft;
    DROP TABLE cright;

## Reduce-side Join Example
---

### Reduce-side.hql

Create two external tables called "left" and "right" with the specified columns. The rest of the table CREATE statement indicates the fields from the dataset are separated by commas. The LOCATION shows the HDFS directory where the data is stored.

    CREATE EXTERNAL TABLE left (key INT, value STRING)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    STORED AS TEXTFILE 
    LOCATION '/user/brad/input/left';
    
    CREATE EXTERNAL TABLE right (key INT, value STRING)
    ROW FORMAT DELIMITED
    FIELDS TERMINATED BY ','
    STORED AS TEXTFILE
    LOCATION '/user/brad/input/right';

Now we want to make sure the contents of the table were loaded correctly, so we run these commands:

**Table left**

    SELECT * FROM left;
    
    1       A
    1       B
    4       A
    2       C
    5       D
    3       A
    3       B
    6       A
    
**Table right**

    SELECT * FROM right;
    
    1       X
    4       Y
    2       X
    5       Y
    5       Z 
    3       X
    3       Z

We then perform the reduce-side join.

    SELECT left.key, left.value, right.value 
    FROM left, right 
    WHERE left.key == right.key;
    
    1       A       X
    1       B       X
    4       A       Y
    2       C       X
    5       D       Y
    5       D       Z
    3       A       X
    3       A       Z
    3       B       X
    3       B       Z

## Bucket Map Join Example

Since we want to bucket the data by join keys to allow the join to be processed in parallel, we will have to activate this approach by executing the following commands. For this to happen, the number of buckets in one table must be a multiple of the number of buckets in the other table. This means that the mapper processing the bucket 1 from cleft will only fetch bucket 1 for cright to join. It also sets the number of map tasks to be equal to the number of buckets. In this example, the number of buckets is 3.

    SET hive.optimize.bucketmapjoin=true;
    SET hive.enforce.bucketmapjoin=true;
    SET hive.enforce.bucketing=true;

Create two tables into 3 buckets for each table, cleft and cright

    CREATE TABLE cleft (key INT, value STRING)
    CLUSTERED BY (key)
    SORTED BY (key) INTO 3 BUCKETS;
    
    CREATE TABLE cright (key INT, value STRING)
    CLUSTERED BY (key)
    SORTED BY (key) INTO 3 BUCKETS;

Now we populate the table with data from the first two tables, right and left.

    INSERT INTO cleft SELECT * from left;
    INSERT INTO cright SELECT * from right;

We then perform the join.
 
    SELECT cleft.key, cleft.value, cright.value
    FROM cleft, cright
    WHERE cleft.key == cright.key;

### Results ###

Since we have 3 reducers, the results are obtained based on the hash key provided by the 3 mappers after sorting and shuffling phase.

**First reducer 0**

    cleft0 cright0
    
     6,A    3,Z
     3,B    3,X
     3,A
**Second reducer 1**

    cleft1 cright1
    
     4,A    4,Y
     1,B    1,X
     1,A
**Third reducer 2**

    cleft2 cright2
    
     5,D    5,Z
     2,C    5,Y
     2,X

So the join result, with all three buckets in parallel (3 mappers) is:

    3   B   Z
    3   B   X
    3   A   Z
    3   A   X
    4   A   Y
    1   B   X
    1   A   X
    5   D   Z
    5   D   Y
    2   C   X
