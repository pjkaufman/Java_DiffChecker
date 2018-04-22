ECHO ON

javac -cp ".;..\jarLibrary\jackson-annotations-2.0.1.jar";".;..\jarLibrary\jackson-databind-2.0.1.jar";".;..\jarLibrary\jackson-core-2.0.1.jar";".;..\jarLibrary\mysql-connector-java-5.1.44-bin.jar" ..\db_diff_checker_gui2\*.java
jar cvfm ..\run\Db_Diff_Checker.jar ..\manifest.mf ..\db_diff_checker_gui2\ ..\Images ..\jarLibrary
del ..\db_diff_checker_gui2\*.class
cls && cls
