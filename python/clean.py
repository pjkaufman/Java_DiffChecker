import os
import platform
from subprocess import call
import shutil

OS = platform.system()
#check the os to see which part of the code to run
if ( OS == 'Windows' ):
    #remove all class files
    os.chdir( "..\\test\\db_diff_checker_gui2" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".class") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    os.chdir("..\\")
    try:

        shutil.rmtree('com')
    except:
        pass
    os.chdir('..\\python')
else:
    #remove all class files
    os.chdir( "../test/db_diff_checker_gui2" )
    filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith(".class") ]
    for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))
    os.chdir("../")
    try:

        shutil.rmtree('com')
    except:
        pass
    os.chdir('../python')
