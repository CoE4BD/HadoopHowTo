#Setup Eclipse for Hadoop Development Using Maven on Windows

Julian Mino  
10/17/2013

### Using the software lifecycle and build tool Maven, you can configure Eclipse for Hadoop development in minutes.
---
*Note: This document assumes a package name of edu.stthomas.gps and a project name of SEIS736. Please change accordingly to meet your requirements.*

### Prerequisites

1. Download and Install JDK
2. Set JAVA_HOME 

	*Click [here](http://docs.oracle.com/cd/E21454_01/html/821-2532/inst_cli_jdk_javahome_t.html#inst_cli_set_jdk_windows_t) for detailed instructions.*
	
3. Install PuTTy

	*To make things easier, use the Windows installer for everything except PuTTYtel. Follow this [link](http://www.chiark.greenend.org.uk/~sgtatham/putty/download.html) to download the Windows installer.*

### Install Maven

1. Download and Install Maven
2. Set MAVEN_HOME

	Click [here](http://maven.apache.org/download.cgi#Installation) for detailed instructions.
	
3. Check the installation by executing `mvn -v` in the command prompt. The command prompt will display the following information.

		Apache Maven 3.1.0 (893ca28a1da9d5f51ac03827af98bb730128f9f2; 2013-06-27 21:15:32-0500)
		Maven home: c:\apache-maven-3.1.0
		Java version: 1.7.0_25, vendor: Oracle Corporation
		Java home: c:\Progra~1\Java\jdk1.7.0_25\jre
		Default locale: en_US, platform encoding: Cp1252
		OS name: "windows 7", version: "6.1", arch: "amd64", family: "windows"

### Install Eclipse

Download and install Eclipse IDE for Java Developers. You may download the IDE from [www.eclipse.org](www.eclipse.org).

### Integrate Maven with Eclipse

Change your working directory to Eclipse's workspace directory. This guide assumes the Eclipse workspace directory is in your home folder. (Replace username with your actual user name.)

`
C:\Users\username> cd workspace
`

Enter the following command.

	C:\Users\username\workspace> mvn archetype:generate ^
	-DgroupId=edu.stthomas.gps -DartifactId=SEIS736 ^
	-DarchetypeArtifactId=maven-archetype-quickstart ^
	-DinteractiveMode=false

*Note: The caret at the end of the first three lines means the command is not complete. It is used for a long command, if you would like to enter the command on multiple lines use the caret. If you type the command on one line, you should not type caret. Also, note that if copy and paste the above command into the command prompt then it will appear as follows:*

	C:\Users\username\workspace> mvn archetype:generate ^
	More? -DgroupId=edu.stthomas.gps -DartifactId=SEIS736 ^
	More? -DarchetypeArtifactId=maven-archetype-quickstart ^
	More? -DinteractiveMode=false

*"More?" is added by the command prompt. (No need to type "More?".)*

Once the command has finished executing a new directory (SEIS736) is created within the current directory (workspace). Go to the newly created directory.

`
C:\Users\username\workspace> cd SEIS736
`

Enter the following command (make sure you have access to the Internet):

	C:\Users\username\workspace\SEIS736> mvn -Declipse.workspace=C:/Users/username/workspace ^
	eclipse:configure-workspace eclipse:eclipse


*Note: Remember to replace 'username' with your real user name*

Replace the pom.xml file (under the directory SEIS736) with this [pom.xml](https://github.com/CoE4BD/HadoopHowTo/blob/master/hadoopMaven/pom.xml) file.
Open the xml file and change upload.sh to upload.bat. 

Execute the following command:

`
C:\Users\username\workspace\SEIS736> mvn eclipse:clean eclipse:eclipse
`

### Import project into Eclipse

Start eclipse, and select File->Import->Existing Projects Into Workspace.

Choose SEIS736 and import it into Eclipse.

Create upload.bat in the Eclipse project folder (same level as the pom.xml file).

Paste the following contents into the upload.bat file.

	pscp -r C:/Users/username/workspace/SEIS736/target/SEIS736-1.0.jar username@127.0.0.1:/home/username/

*Note: You need to update the above command accordingly.*
	
### Package the project

Enter the following command

`
C:\Users\username\workspace\SEIS736> mvn package
`

In Eclipse, right click SEIS736 and select Refresh. Then you will see a jar file named SEIS736-1.0.jar that was generated in the target folder.

### Upload jar file to the server

`
C:\Users\username\workspace\SEIS736> mvn install
`

####References

1. [To Install the JDK Software and Set JAVA_HOME on a Windows System](http://docs.oracle.com/cd/E21454_01/html/821-2532/inst_cli_jdk_javahome_t.html#inst_cli_set_jdk_windows_t)

2. [Download Apache Maven](http://maven.apache.org/download.cgi#Installation)

4. [Developing CDH Applications with Maven and Eclipse](http://blog.cloudera.com/blog/2012/08/developing-cdh-applications-with-maven-and-eclipse/)
