#!/bin/bash

javac -cp ".;../jarLibrary/jackson-annotations-2.0.1.jar";".;../jarLibrary/jackson-databind-2.0.1.jar";".;../jarLibrary/jackson-core-2.0.1.jar";".;../jarLibrary/mysql-connector-java-8.0.11-bin.jar" ../db_diff_checker_gui2/*.java
jar cvfm ../run/Db_Diff_Checker.jar ../manifest.mf ../db_diff_checker_gui2/ ../Images ../jarLibrary
rm -f ../db_diff_checker_gui2/*.class
clear && clear
