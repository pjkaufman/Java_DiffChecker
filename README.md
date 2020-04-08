# Database Difference Checker - Database Updater [![Current Issues](https://img.shields.io/github/issues/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/issues)  [![License](https://img.shields.io/github/license/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/blob/master/LICENSE)
###### By Peter Kaufman

## Description

Compares two databases and generates the statements that are needed to make the two databases the same. The statements can then be run or just copied to review or run later.

## Table of Contents

* [Installation](#installation)
* [Usage](#usage)
* [Supported Database Implimentations](#supported-database-implimentations)
* [Features](#features)
* [Documentation](#documentation)
* [License](#license)
 
## Installation

### Dependencies

1. [Java](https://java.com/en/download/)
2. [Python 2.7 or later](https://www.python.org/downloads/)
3. Some kind of hosting service for the database implementation if need:
    * MySQL - one option is [WAMP](http://wampserver.aviatechno.net/)
      * For development I recommend [MySQL Workbench](https://dev.mysql.com/downloads/workbench/)
    * SQLite - NA since your computer hosts the database.
      * For development I recommend [SQLiteStudio](https://sqlitestudio.pl/index.rvt?act=download)
    * Couchbase - one option is [Couchbase Server Community Edition](https://www.couchbase.com/downloads)
    * MongoDB - one option is [MongoDB Compass Community](https://www.mongodb.com/download-center/community)

_Note: the python scripts have only been tested in Python 2.7 and 3.7_

Clone this repo by running `git clone https://github.com/pjkaufman/Java_DiffChecker.git`.

Make sure that Python and the jre paths have been added to your PATH variable.

[Back to Table of Contents](#table-of-contents)

## Usage

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

[Back to Table of Contents](#table-of-contents)

## Supported Database Implimentations

* SQL
  * MySQL
    * Tables, Columns, Indices, and Views (non-nested) can be compared
  * SQLite
    * Tables, Columns, Indices, and Views (non-nested) can be compared
* NoSQL
  * Couchbase
    * Documents, and Indices can be compared
  * MongoDB
    * Collections can be compared

[Back to Table of Contents](#table-of-contents)

## Features
**1.Two Connection Database Comparison**
    
This type of database comparison connects to two databases and compares them yeilding the SQL statements to make them the same.

<<<<<<< HEAD
When this option is selected in the application it will take you to one of the following screens:
<center>
  <p>MySQL:</p>
  <img src="UserGuides\images\twoDBComparison.png" alt="Two Database Comparison MySQL Screen" height = "300"/>
  <p>SQLite:</p>
  <img src="UserGuides\images\twoDBComparisonSQLite.png" alt="Two Database Comparison SQLite Screen" height = "300"/>
</center>
Fill out each of the needed fields and click Compare.
=======
When going to this tab, the user input forms are generated dynamically based on the database implementation selected and look something like this:

<img src="UserGuides\images\twoDBComparison.png" alt="Two Database Comparison MySQL Screen" height = "300"/>

Fill out each of the needed fields and click Generate Statements.
>>>>>>> A refactor and test still needs to be done, but the README should have changes along with the correct photos.

*Note: the default port for MYSQL is 3306 and that the development database information goes on the LEFT and the live database information goes on the RIGHT*

**2.One Connection Database Comparison**

This type of database comparison connects to one database and uses a serialized file to compare them yeilding the SQL statements to make them the same.

<<<<<<< HEAD
When this option is selected in the application it will take you to the following screen:
<center>
  </p>MySQL:<p>
  <img src="UserGuides\images\oneDBComparison.png" alt="One Database Comparison MySQL Screen" height = "300"/>
  <p>SQLite:</p>
  <img src="UserGuides\images\oneDBComparisonSQLite.png" alt="One Database Comparison SQLite Screen" height = "300"/>
</center>
=======
When going to this tab, the user input forms are generated dynamically based on the database implementation selected and look something like this:

<img src="UserGuides\images\oneDBComparison.png" alt="One Database Comparison MySQL Screen" height = "300"/>
>>>>>>> A refactor and test still needs to be done, but the README should have changes along with the correct photos.
  
Fill out each of the needed fields and click Generate Statments.

*Note: the default port for MYSQL is 3306 and that the development database is the serialized database*

**3.Database Snapshot**
    
A database snapshot is where a "copy" of the schema structure for the database is written to a serialized file for later use. It stores information such as tables, indexes, views, columns, and other pertinent data for database comparisons.

<<<<<<< HEAD
When this option is selected in the application it will take you to the following screen:
<center>
  <p>MySQL:</p>
  <img src="UserGuides\images\takeDBSnapshot.png" alt="Database Snapshot MySQL Screen" height = "300"/>
  <p>SQLite:</p>
  <img src="UserGuides\images\takeDBSnapshotSQLite.png" alt="One Database Comparison SQLite Screen" height = "300"/>
</center>
Fill out each of the needed fields and click Snapshot. 
=======
When going to this tab, the user input forms are generated dynamically based on the database implementation selected and look something like this:

<img src="UserGuides\images\takeDBSnapshot.png" alt="Database Snapshot MySQL Screen" height = "300"/>

Fill out each of the needed fields and click Take Snapshot. 
>>>>>>> A refactor and test still needs to be done, but the README should have changes along with the correct photos.

*Note: the default port for MYSQL is 3306, and this option is for the development database*
   
4.Review Last Set of SQL Statements

This option allows the user to see the last set of SQL statements that were run.
After selecting this option, you will see something similar to this:
<<<<<<< HEAD
<center align="center">
  <img src="UserGuides\images\lastSQLSet.png" alt="Last Set of SQL Statments Generated" height = "300"/>
</center>
=======
>>>>>>> A refactor and test still needs to be done, but the README should have changes along with the correct photos.

<img src="UserGuides\images\lastSQLSet.png" alt="Last Set of Statments Generated" height = "300"/>

**5.Review Logs**

The review logs contain information such as stack traces for errors that have occured or how long a database comparison or SQL run took.

After selecting this option, you will see something similar to this:
<img src="UserGuides\images\runLog.png" alt="Run Logs" height = "300"/>

[Back to Table of Contents](#table-of-contents)

## Documentation

### How It Works

1. [V1.0.0](UserGuides/pdf/DatabaseDifferenceCheckerReport.pdf)
2. [V2.0.0](UserGuides/pdf/DatabaseDifferenceCheckerReportV2.0.0.pdf)
3. [V2.0.1](UserGuides/pdf/DatabaseDifferenceCheckerReportV2.0.1.pdf)

### Java Documentation/API

Java documentation can be found [here](https://pjkaufman.github.io/Java_DiffChecker/).

[Back to Table of Contents](#table-of-contents)

### Error Code List

The error codes can be found [here](https://github.com/pjkaufman/Java_DiffChecker/wiki/Error-Codes).

## License

[MIT](LICENSE)

[Back to Table of Contents](#table-of-contents)
