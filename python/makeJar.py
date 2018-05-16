import os
import platform
from subprocess import call
from subprocess import check_output
from shutil import copy

OS = platform.system()
p = ""
#update the current Manifest
execfile( "manifest.py" )
#check the os to see which part of the code to run
if ( OS == 'Windows' ):
    #change directory to jar file location
    os.chdir( "..\\jarLibrary" )
    #get current jar list and add it to the Class-Path
    filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith(".jar") ]
    for fi in filelist:
        p += "\".;..\\jarLibrary\\" + fi + "\";"
    p = p[:-1]
    #move back to the python directory
    os.chdir( "..\\python" )
    #compile the java files and make the jar file
    try:
        check_output( 'javac -Xlint:unchecked -cp ' + p + ' ..\\db_diff_checker_gui2\\*.java' )
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
    #move back to the python directory
    os.chdir( "../python" )
    #compile the java files and make the jar file
    try:
        check_output( 'javac -Xlint:unchecked -cp ' + p + ' ../db_diff_checker_gui2/*.java' )
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
    except:
        print "An error occurred during compilation"
