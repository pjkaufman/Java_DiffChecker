from subprocess import call
import platform

OS = platform.system()
if ( OS == "Windows" ):

    call( "java -jar ..\\run\\Db_Diff_Checker.jar" )
else:
    call( "java -jar ../run/Db_Diff_Checker.jar" )
