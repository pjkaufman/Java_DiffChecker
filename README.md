# Database Difference Checker - Database Updater [![Current Issues](https://img.shields.io/github/issues/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/issues)  [![License](https://img.shields.io/github/license/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/blob/master/LICENSE)
###### By Peter Kaufman

## Description

 Compares MySQL or SQLite databases and generates the SQL statements that are needed to make the two databases the same. The SQL statements can then be run or just copied to review or run later.

<h2 id="table_of_contents">Table of Contents</h2>

<ul> 
  <li>
    <a href="#installation">Installation</a>
  </li>
  <li>
    <a href="#usage">Usage</a>
  </li>
  <li>
    <a href="#features">Features</a>
  </li>
  <li>
    <a href="#documentation">Documentation</a>
  </li>
  <li>
    <a href="#license">License</a>
  </li>
</ul>

<h2 id="installation"> Installation </h2>

### Dependencies

<ol>
  <li>
    <a href="https://java.com/en/download/">Java</a>
  </li>
  <li>
    <a href="https://www.python.org/downloads/">Python 2.7 or later</a>
  </li>
  <li>
    Some kind of hosting service for MySQL such as <a href="http://wampserver.aviatechno.net/">WAMP</a>
  </li>
</ol>

_Note: the python scripts have only been tested in Python 2.7 and 3.7_

Clone this repo by running `git clone https://github.com/pjkaufman/Java_DiffChecker.git`.

Make sure that Python and the jre paths have been added to your PATH variable.

<a href="#table_of_contents">Back to Table of Contents</a>

<h2 id="usage">Usage</h2>

Go to the base directory and run

```
python routines.py
```

You should see

``` 
Routine Options
run - makes and runs the JAR file
push - commits the current repo and pushes it
debug - runs the current code base for testing
test - runs the unit tests on the source code
clean - deletes the test, logs, and build directories
Enter desired option:
```

### Running 

When prompted by the script for a routine to run, type 'run'.

If there are no errors you will see the following GUI:

<div align="center">
  <img src="UserGuides\images\runResult.png" alt="Database Difference Checker Home" height = "300"/>
</div>

### Testing

After modifying any of the Java files in the repository, you can go to the base directory of this repository and run 

```
python routines.py
```

When prompted for the routine to run, enter 'debug'. This will compile all of the current Java files into the test/dbdiffchecker folder where it will be run if no errors occur (no JAR file will be created).

### Manually Running The JAR File

In order to use the JAR file with logs, make sure that where you run the jar file you have a logs folder. Also make sure that the jar file is located in the same directory as the lib folder containing the jar files for the reopsitory.

To run the applciation through the JAR file run

```
java -jar path_to_jar_file
```

<a href="#table_of_contents">Back to Table of Contents</a>

<h2 id="features">Features</h2>

<ol> 
  <li>
    Two Connection Database Comparison
    <p>This type of database comparison connects to two databases and compares them yeilding the SQL statements to make them the same.</p>
      <p>When this option is selected in the application it will take you to one of the following screens:
        <div align="center">
          <p>MySQL:</p>
          <img src="UserGuides\images\twoDBComparison.png" alt="Two Database Comparison MySQL Screen" height = "300"/>
          <p>SQLite:</p>
          <img src="UserGuides\images\twoDBComparisonSQLite.png" alt="Two Database Comparison SQLite Screen" height = "300"/>
        </div>
        Fill out each of the needed fields and click Compare. <br>
        <i>Note: the default port for MYSQL is 3306 and that the development database information goes on the LEFT and the live database information goes on the RIGHT</i>
      </p>
  </li>
  <li>
    One Connection Database Comparison
    <p>This type of database comparison connects to one database and uses a serialized file to compare them yeilding the SQL statements to make them the same.</p>
      <p>When this option is selected in the application it will take you to the following screen:
        <div align="center">
          <p>MySQL:</p>
          <img src="UserGuides\images\oneDBComparison.png" alt="One Database Comparison MySQL Screen" height = "300"/>
          <p>SQLite:</p>
          <img src="UserGuides\images\oneDBComparisonSQLite.png" alt="One Database Comparison SQLite Screen" height = "300"/>
        </div>
        Fill out each of the needed fields and click Compare. <br>
        <i>Note: the default port for MYSQL is 3306 and that the development database is the serialized database</i>
      </p>
  </li>
  <li>
    Database Snapshot
    <p>A database snapshot is where a "copy" of the schema structure for the database is written to a serialized file for later use. It stores information such as tables, indexes, views, columns, and other pertinent data for database comparisons.</p>
    <p>When this option is selected in the application it will take you to the following screen:
      <div align="center">
        <p>MySQL:</p>
          <img src="UserGuides\images\takeDBSnapshot.png" alt="Database Snapshot MySQL Screen" height = "300"/>
          <p>SQLite:</p>
          <img src="UserGuides\images\takeDBSnapshotSQLite.png" alt="One Database Comparison SQLite Screen" height = "300"/>        
      </div>
      Fill out each of the needed fields and click Snapshot. <br>
      <i>Note: the default port for MYSQL is 3306, and this option is for the development database</i>
    </p>
  </li>
  <li>
    Review Last Set of SQL Statements
    <p>This option allows the user to see the last set of SQL statements that were run.</p>
    <p>After selecting this option, you will see something similar to this:
      <div align="center">
        <img src="UserGuides\images\lastSQLSet.png" alt="Last Set of SQL Statments Generated" height = "300"/>
      </div>
    </p>
  </li>
  <li>
    Review Logs
    <p>The review logs contain information such as stack traces for errors that have occured or how long a database comparison or SQL run took.</p>
    <p>After selecting this option, you will see something similar to this:
      <div align="center">
        <img src="UserGuides\images\runLog.png" alt="Run Logs" height = "300"/>
      </div>
    </p>
  </li>
</ol>

<a href="#table_of_contents">Back to Table of Contents</a>

<h2 id="documentation">Documentation</h2>

### How It Works

<ol>
  <li>
    <a href="UserGuides/pdf/DatabaseDifferenceCheckerReport.pdf">V1.0.0</a>
  </li>
  <li>
    <a href="UserGuides/pdf/DatabaseDifferenceCheckerReportV2.0.0.pdf">V2.0.0</a>
  </li>
  <li>
    <a href="UserGuides/pdf/DatabaseDifferenceCheckerReportV2.0.1.pdf">V2.0.1</a>
  </li>
</ol>

### Java Documentation/API

Java documentation can be found [here](https://pjkaufman.github.io/Java_DiffChecker/).

<a href="#table_of_contents">Back to Table of Contents</a>

<h2 id="license">License</h2>

<a href="LICENSE">MIT</a>

<a href="#table_of_contents">Back to Table of Contents</a>