# Java_DiffChecker [![Repo Size](https://reposs.herokuapp.com/?path=pjkaufman/Java_DiffChecker)](https://github.com/pjkaufman/Java_DiffChecker)  [![Current Issues](https://img.shields.io/github/issues/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/issues)  [![License](https://img.shields.io/github/license/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/blob/master/LICENSE)
###### By Peter Kaufman

## Description

Java_DiffChecker is a Java program compares MySQL databases and generates the SQL statements that are needed to make the two databases the same. The SQL statements can then be run or just copied to review later.

## Table of Contents

- [Installation](https://github.com/pjkaufman/Java_DiffChecker#installation)
- [Usage](https://github.com/pjkaufman/Java_DiffChecker#usage)
- [Documentation](https://github.com/pjkaufman/Java_DiffChecker#documentation)
- [License](https://github.com/pjkaufman/Java_DiffChecker#license)

## Installation

### Dependencies

1. [Java](https://java.com/en/download/)
2. [Python 2.7](https://www.python.org/downloads/)
3. Some kind of hosting service hosting your MySQL databse such as [WAMP](http://wampserver.aviatechno.net/).

_Note: the python scripts have only been tested in Python 2.7_

Clone this repo by running `git clone https://github.com/pjkaufman/Java_DiffChecker.git`.

Make sure that Python and the jre paths have been added to your PATH variable.

## Usage

Go to the python folder and run `python routines.py`. 

There will be several options which include 'run' and 'debug'. 

### Running 

You can go to the python folder and run `python routines.py`.

When prompted by the script for a routine to run type 'run'.

Watch the output. If there is an error check to see what the error was. Otherwise the jar file that was created in the build folder will be executed.

### Testing

After modifying any of the Java files in the repository, you can go to the python folder and run `python routines.py`.

Enter 'debug' when prompted for a routine to run. This will compile all of the current Java files into the test/db_diff_checker_gui2 folder where it will be run if no errors occur.

## Documentation

### How It Works

- [V1.0.0](https://github.com/pjkaufman/Java_DiffChecker/tree/master/pdf/DatabaseDifferenceCheckerReport.pdf)
- [V2.0.0](https://github.com/pjkaufman/Java_DiffChecker/tree/master/pdf/DatabaseDifferenceCheckerReportV2.0.0.pdf)

### Java Documentation 

Java documentation can be found [here](https://pjkaufman.github.io/Java_DiffChecker/).

## License

[MIT](https://github.com/pjkaufman/Java_DiffChecker/blob/master/LICENSE) 