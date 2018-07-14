# Java_DiffChecker [![Repo Size](https://reposs.herokuapp.com/?path=pjkaufman/Java_DiffChecker)](https://github.com/pjkaufman/Java_DiffChecker)  [![Current Issues](https://img.shields.io/github/issues/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/issues)  [![License](https://img.shields.io/github/license/pjkaufman/Java_DiffChecker.svg)](https://github.com/pjkaufman/Java_DiffChecker/blob/master/LICENSE)
###### By Peter Kaufman

This repo is a project that compares MySQL databases using Java.

## Instalation

Make sure that you have [Java](https://java.com/en/download/) and [Python](https://www.python.org/downloads/) 2.7 or higher installed.

_Note: the python scripts have only been tested in Python 2.7_

Clone this repo by running `git clone https://github.com/pjkaufman/Java_DiffChecker.git`.

Make sure that Python and the jre paths have been added to your path variable.

## Running

Go to the python folder and run  `python routines.py`. 

When you are prompted by the script for a command, type in 'run'.

Watch the output. If there is an error check to see what the error was. Otherwise the jar file that was created in the build folder will be executed.

## Testing

After modifying any of the Java files in the repository, you can go to the python folder and run `python routines.py`.

Enter 'debug' when prompted for a routine to run. This will compile all of the current Java files into the test/db_diff_checker_gui2 folder where it will be run if no errors occur.

## Java Documentation

Java documentation can be found [here](https://pjkaufman.github.io/Java_DiffChecker/).

## Dependencies

1. [Java](https://java.com/en/)
2. [Python 2.7](https://www.python.org/)