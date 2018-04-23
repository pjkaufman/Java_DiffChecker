from subprocess import call
import platform
import os
#make the manfest file
OS = platform.system()
if ( OS == "Windows" ):

    f = open( "..\\manifest.mf", "w+" )
else:

    f = open( "../manifest.mf", "w+" )
#add the proper verion info
f.write( "Manifest-Version: 1.0\n" )
f.write( "Ant-Version: Apache Ant 1.9.7\n" )
cP = "Class-Path: "
mC = "Main-Class: db_diff_checker_gui2.DB_Diff_Checker_GUI"

if ( OS == "Windows" ):
    #change directory to jar file location
    os.chdir( "..\\jarLibrary" )
    #get current jar list and add it to the Class-Path
    filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith(".jar") ]
    for fi in filelist:
        cP += "lib\\" + fi + " "
    #move back to the python directory
    os.chdir( "..\\python" )
else:
    #change directory to jar file location
    os.chdir( "../jarLibrary" )
    #get current jar list and add it to the Class-Path
    filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith(".jar") ]
    for fi in filelist:
        cP += "/lib/" + fi + " "
    #move back to the python directory
    os.chdir( "../python" )
#Class-Path is added if a jar file was found
if ( cP != "Class-Path: " ):
    f.write( cP  + "\n" )
f.write( mC  + "\n")
f.close()
