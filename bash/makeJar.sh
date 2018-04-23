#!/bin/bash

javac -cp ".;../jarLibrary/jackson-annotations-2.0.1.jar";".;../jarLibrary/jackson-databind-2.0.1.jar";".;../jarLibrary/jackson-core-2.0.1.jar";".;../jarLibrary/mysql-connector-java-8.0.11.jar" ../db_diff_checker_gui2/*.java
jar cvfm ../run/Db_Diff_Checker.jar ../manifest.mf ../db_diff_checker_gui2/ ../Images ../jarLibrary
rm -fs ../db_diff_checker_gui2/*.class
rm -fs ../run/lib/*.jar
cp ../jarLibrary/*.jar ../run/lib/
clear && clear
