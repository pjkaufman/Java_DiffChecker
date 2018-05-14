import os
import platform
from subprocess import call
from subprocess import check_output
from shutil import copy

OS = platform.system()
p = ""
#check the os to see which part of the code to run
if ( OS == 'Windows' ):
    #change directory to jar file location
    os.chdir( "..\\jarLibrary" )
    #get current jar list and add it to the Class-Path
    filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith(".jar") ]
    for fi in filelist:
        p += "\".;..\\jarLibrary\\" + fi + "\";"
    p = p[:-1]
    print "Set up class path"
    #move back to the python directory
    os.chdir( "..\\python" )
    #compile the java files and make the jar file
    try:
        check_output( 'javac -d ..\\test -cp ' + p + ' ..\\db_diff_checker_gui2\\*.java' )
        os.chdir( ".." )
        print "Compiled files"
        #run compiled files with classPath
        #move to run directory
        os.chdir( "test" )
        call( "java -cp " + p + " db_diff_checker_gui2.DB_Diff_Checker_GUI" )
        #move back to the python directory
        os.chdir( "..\\python" )
    except:
        print "An error occurred during compilation"
else:
    #change directory to jar file location
    os.chdir( "../jarLibrary" )
    #get current jar list and add it to the Class-Path
    filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith(".jar") ]
    for fi in filelist:
        p += "\".;../jarLibrary/" + fi + "\";"
    p = p[:-1]
    print "Set up class path"
    #move back to the python directory
    os.chdir( "../python" )
    #compile the java files and make the jar file
    try:
        check_output( 'javac -d ../test -cp ' + p + ' ../db_diff_checker_gui2/*.java' )
        print "Compiled files"
        os.chdir( ".." )
        #run compiled files with classPath
        #move to run directory
        os.chdir( "test" )
        call( "java -cp " + p + " db_diff_checker_gui2.DB_Diff_Checker_GUI" )
        #move back to the python directory
        os.chdir( "../python" )
    except:
        print "An error occurred during compilation"
