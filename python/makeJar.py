import os
import platform
from subprocess import call
from shutil import copy

OS = platform.system()
#update the current Manifest
execfile( "manifest.py" )
#check the os to see which part of the code to run
if ( OS == 'Windows' ):
    #compile the java files and make the jar file
    call( 'javac -cp ".;..\\jarLibrary\\jackson-annotations-2.0.1.jar";".;..\\jarLibrary\\jackson-databind-2.0.1.jar";".;..\\jarLibrary\\jackson-core-2.0.1.jar";".;..\\jarLibrary\\mysql-connector-java-8.0.11.jar" ..\\db_diff_checker_gui2\\*.java' )
    call( 'jar cvfm ..\\run\\Db_Diff_Checker.jar ..\\manifest.mf ..\\db_diff_checker_gui2\\ ..\\Images ..\\jarLibrary' )
    #remove unnecesssary .class files
    os.chdir( "..\\db_diff_checker_gui2\\" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".class") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    #remove old jar file list
    os.chdir( "..\\run\\lib\\" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".jar") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    os.chdir("..\\..\\")
    #copy current jar list to the \\run\\lib
    filelist = [ f for f in os.listdir(os.path.join(os.getcwd(), "jarLibrary\\" )) if f.endswith(".jar") ]
    for f in filelist:
        copy( os.path.join(os.getcwd(), "jarLibrary\\" + f), os.getcwd() + "\\run\\lib\\" )
        os.system('cls')
else:
    #compile the java files and make the jar file
    call( 'javac -cp ".;../jarLibrary/jackson-annotations-2.0.1.jar";".;../jackson-databind-2.0.1.jar";".;../jarLibrary/jackson-core-2.0.1.jar";".;../jarLibrary/mysql-connector-java-8.0.11.jar" ../db_diff_checker_gui2/*.java' )
    call( 'jar cvfm ../run/Db_Diff_Checker.jar ../manifest.mf ../db_diff_checker_gui2/ ../Images ../jarLibrary' )
    #remove unnecesssary .class files
    os.chdir( "../db_diff_checker_gui2/" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".class") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    #remove old jar file list
    os.chdir( "../run/lib/" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".jar") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    os.chdir("../../")
    #copy current jar list to the /run/lib
    filelist = [ f for f in os.listdir(os.path.join(os.getcwd(), "jarLibrary/" )) if f.endswith(".jar") ]
    for f in filelist:
        copy( os.path.join(os.getcwd(), "jarLibrary/" + f), os.getcwd() + "/run/lib/" )
        os.system('clear')
