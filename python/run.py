from subprocess import call
import os
import platform

OS = platform.system()
if ( OS == "Windows" ):
    #move to run directory
    os.chdir( "..\\run" )
    call( "java -jar ..\\run\\Db_Diff_Checker.jar" )
    #move back to the python directory
    os.chdir( "..\\python" )
else:
    #move to run directory
    os.chdir( "../run" )
    call( "java -jar ../run/Db_Diff_Checker.jar" )
    #move back to the python directory
    os.chdir( "../python" )
