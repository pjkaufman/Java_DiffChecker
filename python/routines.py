import os
import platform
from subprocess import call
from subprocess import check_output
import shutil
from shutil import copy
class Routines:
    #instance variables
    __OS = ''

    #__init__ is the constructor which initializes all instance variables
    def __init__( self ):
        self.__OS = platform.system()
        return None

    #getOS gets the OS variable
    def getOS( self ):
        return self.__OS

    #formatStr takes a string and converts all ','s to either '\\' or '/'
    #param: str is the string to have all commas replaced with either '\\' or '/'
    def formatStr( self, str ):
        if( self.getOS() == 'Windows'):
            return str.replace( ',', "\\")    
        else:
            return str.replace( ',', '/') 

    #chdir takes a string which is converted to the right format and then run
    # param dirStr is a string which represents a directory change string (for example: "..,bin")
    def chdir( self, dirStr ):
        #format and run the change directory string
        os.chdir(  self.formatStr( dirStr ))
        return None

    #compile compiles java files and determines where to send the output
    #param: path is the path to where to send the class files
    #param: cp is the classPath for where the JAR files are stored
    #param: type is either True or False to determine whether or not to compile
    #the class files to the provided path
    def compile( self, path, cp, type ):
        start = 'javac -Xlint:unchecked '
        if( type == True ):
            start += '-d ' + path

        #compile the java files and make the jar file
        try:
            check_output( self.formatStr( start + ' -cp ' + cp + ' ..,db_diff_checker_gui2,*.java') )
            print 'Compiled files'
        except:
            print 'An error occurred during compilation'
            return False
        return True

    #makeJar sets up the manifest and makes the JAR file
    def makeJar( self ):
        self.updateManifest()
        p = ''
        #change directory to jar file location
        self.chdir( '..,jarLibrary' )
        #get current jar list and add it to the Class-Path
        filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith( '.jar' ) ]
        for fi in filelist:
            p += self.formatStr( "\".;..,jarLibrary," + fi + "\";" )
        p = p[:-1]
        #move back to the python directory
        self.chdir( '..,python' )
        #compile the java files and make the jar file
        if( self.compile( self.formatStr( '..,db_diff_checker_gui2' ), p, False )):
            call( self.formatStr( 'jar cvfm ..,run,Db_Diff_Checker.jar ..,manifest.mf ..,db_diff_checker_gui2, ..,Images ..,jarLibrary' ))
            #remove unnecesssary .class files
            self.chdir( '..,db_diff_checker_gui2' )
            filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith( '.class' ) ]
            for f in filelist:
                os.remove(os.path.join(os.getcwd(), f))
            #remove old jar file list
            self.chdir( '..,run,lib,' )
            filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith( '.jar' ) ]
            for f in filelist:
                os.remove(os.path.join(os.getcwd(), f))
            self.chdir(  '..,..,' )
            #copy current jar list to the lib folder in the run directory
            filelist = [ f for f in os.listdir(os.path.join(os.getcwd(), self.formatStr( 'jarLibrary,' ))) if f.endswith( '.jar' ) ]
            for f in filelist:
                copy( os.path.join(os.getcwd(), self.formatStr( 'jarLibrary,' ) + f), os.getcwd() + self.formatStr( ',run,lib,' ))
            
        #move back to the python directory
        self.chdir( 'python' )
        return None

    #debug sets up a debuggin environment for the current code base
    def debug( self ):
        p = ''
        #change directory to jar file location
        self.chdir( '..,jarLibrary' )
        #get current jar list and add it to the Class-Path
        filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith( '.jar' ) ]
        for fi in filelist:
            p += self.formatStr( "\".;..,jarLibrary," + fi + "\";" )
        p = p[:-1]
        print 'Set up class path'
        #move back to the python directory
        self.chdir( '..,python' )
        #run compiled files with classPath
        if ( self.compile( self.formatStr( '..,test' ), p, True )):
            #move to run directory
            self.chdir( '..,test' )
            call( "java -cp " + p + " db_diff_checker_gui2.DB_Diff_Checker_GUI" )
            #move back to the python directory
            self.chdir( '..,python' )
        return None

    #updateManifest writes to manifest.mf and sets it up for use in a JAR file
    def updateManifest( self ):
        #make the manfest file
        f = open( self.formatStr( '..,manifest.mf' ), 'w+' )
        #add the proper verion info
        f.write( "Manifest-Version: 1.0\n" )
        f.write( "Ant-Version: Apache Ant 1.9.7\n" )
        cP = 'Class-Path: '
        mC = 'Main-Class: db_diff_checker_gui2.DB_Diff_Checker_GUI'
        #change directory to jar file location
        self.chdir( '..,jarLibrary' )
        #get current jar list and add it to the Class-Path
        filelist = [ fi for fi in os.listdir(os.getcwd()) if fi.endswith( '.jar' ) ]
        for fi in filelist:
            cP += self.formatStr( 'lib,' + fi + ' ' )
        #move back to the python directory
        self.chdir( '..,python' )
        #Class-Path is added if a jar file was found
        if ( cP != 'Class-Path: ' ):
            f.write( cP  + "\n" )
        f.write( mC  + "\n")
        f.close()
        return None

    #clean cleans out the test folder of all unnecessary files
    def clean ( self ):
        #remove all class files
        self.chdir( '..,test,db_diff_checker_gui2' )
        filelist = [ f for f in os.listdir(os.getcwd()) if f.endswith( '.class' ) ]
        for f in filelist:
            os.remove(os.path.join(os.getcwd(), f))
        self.chdir( '..,' )
        try:
            #remove unnecessary directory if it exists
            shutil.rmtree( 'com' )
        except:
            pass
        self.chdir( '..,python' )
        return None

    #run makes and runs the JAR file
    def run( self ):
        self.makeJar()
        #move to run directory
        self.chdir( '..,run' )
        call( self.formatStr( 'java -jar ..,run,Db_Diff_Checker.jar' ))
        #move back to the python directory
        self.chdir( '..,python' )
        return None

    #push cleans, documents, and push the repo
    def push( self ):
        #document the repo
        self.document()
        #get rid of unnecessary files
        self.clean()
        #get necessary information from the user
        message = raw_input( 'Commit Message: ' )
        #commit and push the repo
        call( 'git add -A' )
        call( 'git commit -a -m \"' + message + '\"' )
        call( 'git push ')
        return None

    #documnet documents the repo
    def document( self ):
       self.chdir( '..' )
       call( 'javadoc -d "docs" "db_diff_checker_gui2"' )
       self.chdir( 'python' )
       return None

def main():
    routine = Routines()
    print 'Enter one of the following: '
    print 'run - makes and runs the JAR file'
    print 'push - cleans and push the repo'
    print 'debug - runs the current code base for testing'
    rout = raw_input( 'Enter desired option: ' )
    if( rout == 'run' ):
        routine.run()
    elif( rout == 'push' ):
        routine.push()
    elif( rout == 'debug' ):
        routine.debug()

if  __name__ =='__main__':main()
