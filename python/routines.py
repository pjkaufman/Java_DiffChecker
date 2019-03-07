import os
import platform
from subprocess import call
from subprocess import check_output
import shutil
from shutil import copy
class Routines:
  #instance variables
  __OS = ''
  __debugDir = 'test'
  __distrDir = 'build'
  __jarDir = 'jarLibrary'
  __javaCP = ''
  __packageName = 'dbdiffchecker'
  __distrJarDir = 'lib'
  __logFileDir = 'logs'
  __createTest = True
  __createBuild = True

  #__init__ is the constructor which initializes all instance variables
  def __init__(self):
    self.__OS = platform.system()
    #change directory to jar file location
    self.chdir('..,' + self.getJarPath())
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.getcwd()) if fi.endswith('.jar')]
    for fi in filelist:
      self.__javaCP += self.formatStr("\".;..," + self.getJarPath() + "," + fi + "\";")
    self.__javaCP = self.__javaCP[:-1]
    #move back to the python directory
    self.chdir('..,python')
    return None

  #getOSName gets the OS variable
  def getOSName(self):
    return self.__OS

  #getLogFileDirectory gets the name of the log file directory
  def getLogFileDirectory(self):
    return self.__logFileDir

  #getDistributionJarDirectory gets the distribution directory for the JAR files
  def getDistributionJarDirectory(self):
    return self.__distrJarDir

  #getDebugPath retruns a string, which is the path to the debug file destination
  #from a level above where this script is
  def getDebugPath(self):
    return self.__debugDir

  #getDistrubutionPath retruns a string, which is the path to the distribution file destination
  #from a level above where this script is
  def getDistrubutionPath(self):
    return self.__distrDir

  #getJarPath retruns a string, which is the path to the where the jar files to include in the project's
  #jar file from a level above where this script is
  def getJarPath(self):
    return self.__jarDir

  #getPackageName returns a string, which is the name of the package that will be acted upon by this script
  def getPackageName(self):
    return self.__packageName

  #shouldCreateTest is whether or not to create the test directory
  def shouldCreateTest(self):
    return self.__createTest

  #shouldCreateBuild is whether or not to create the build directory
  def shouldCreateBuild(self):
    return self.__createBuild

  #getClassPath is the classpath for java compilation
  def getClassPath(self):
    return self.__javaCP

  #formatStr takes a string and converts all ','s to either '\\' or '/'
  #param: str is the string to have all commas replaced with either '\\' or '/'
  def formatStr(self, str):
    if(self.getOSName() == 'Windows'):
      return str.replace(',', "\\")
    else:
      return str.replace(',', '/')

  #chdir takes a string which is converted to the right format and then changes to the specified directory
  #param dirStr is a string which represents a directory change string (for example: "..,bin")
  def chdir(self, dirStr):
    #format and run the change directory string
    os.chdir( self.formatStr(dirStr))
    return None

  #mkdir takes a string which is converted to the right format and then makes the specified directory
  #param dirStr is a string which represents a directory change string (for example: "..,bin")
  def mkdir(self, dirStr):
    #format and make the directory
    try:
      os.mkdir( self.formatStr(dirStr))
    except OSError:
      pass
    return None

  #compile compiles java files and determines where to send the output
  #param: path is the path to where to send the class files
  #param: type is either True or False to determine whether or not to compile
  #the class files to the provided path
  def compile(self, path, type):
    start = 'javac -Xlint:unchecked '
    if(type == True):
      start += '-d ' + path

    #compile the java files and make the jar file
    try:
      check_output(self.formatStr(start + ' -cp ' + self.getClassPath() + ' ..,' + self.getPackageName() + ',*.java'), shell=True)
      print 'Compiled files'
    except Exception as e:
      print str(e)
      print 'An error occurred during compilation'
      os._exit(1) # stop the program so that the user can try to fix the issue
    return True

  #makeJar sets up the manifest and makes the JAR file
  def makeJar(self):
    self.updateManifest()
    #compile the java files and make the jar file
    if(self.compile(self.formatStr('..,' + self.getPackageName()), False)):
      call(self.formatStr('jar cvfm ..,' + self.getDistrubutionPath() + ',Db_Diff_Checker.jar ..,manifest.mf ..,' + self.getPackageName() + ', ..,Images ..,' + self.getJarPath() + ' ..,' + self.getDistrubutionPath() + ',' + self.getLogFileDirectory() + ','), shell=True)
      #remove unnecesssary directory
      self.__removeDirectory('..,' + self.getDistrubutionPath() + ',' + self.getLogFileDirectory())
      #remove unnecesssary .class files
      self.chdir('..,' + self.getPackageName())
      filelist = [f for f in os.listdir(os.getcwd()) if f.endswith('.class')]
      for f in filelist:
        os.remove(os.path.join(os.getcwd(), f))

    #move back to the python directory
    self.chdir('..,python')
    return None

  #debug sets up a debugging environment for the current code base
  def debug(self):
    #create the test directory
    if (self.shouldCreateTest()):
      self.createTest()
    #run compiled files with classPath
    if (self.compile(self.formatStr('..,test'), True)):
      #move to run directory
      self.chdir('..,test')
      call('java ' + self.getPackageName() + '.DB_Diff_Checker_GUI', shell=True)
      #move back to the python directory
      self.chdir('..,python')
    return None

  #updateManifest writes to manifest.mf and sets it up for use in a JAR file
  def updateManifest(self):
    #make the manfest file
    f = open(self.formatStr('..,manifest.mf'), 'w+')
    #add the proper verion info
    f.write("Manifest-Version: 1.0\n")
    f.write("Ant-Version: Apache Ant 1.9.7\n")
    cP = 'Class-Path: '
    mC = 'Main-Class: ' + self.getPackageName() + '.DB_Diff_Checker_GUI'
    #change directory to jar file location
    self.chdir('..,' + self.getJarPath())
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.getcwd()) if fi.endswith('.jar')]
    for fi in filelist:
      cP += self.formatStr(self.getDistributionJarDirectory() + ',' + fi + ' ')
    #move back to the python directory
    self.chdir('..,python')
    #Class-Path is added if a jar file was found
    if (cP != 'Class-Path: '):
      f.write(cP  + "\n")
    f.write(mC  + "\n")
    f.close()
    return None

  #clean deletes the test and build directories
  def clean(self):
    self.__removeDirectory('..,build')
    self.__removeDirectory('..,test')

  #__removeDirectory takes in a directory and removes it if it exists
  #param: directory is the directory to remove
  def __removeDirectory(self, directory):
    try:
      shutil.rmtree(self.formatStr(directory))
    except:
      pass
    return None

  #run makes and runs the JAR file
  def run(self):
    if (self.shouldCreateBuild()):
      self.createBuild()
    self.makeJar()
    #move to run directory
    self.chdir('..,' + self.getDistrubutionPath())
    call(self.formatStr('java -jar ..,' + self.getDistrubutionPath() +',Db_Diff_Checker.jar'), shell=True)
    #move back to the python directory
    self.chdir('..,python')
    return None

  #push cleans, documents, and push the repo
  def push(self):
    #document the repo
    self.document()
    #get necessary information from the user
    message = raw_input('Commit Message: ')
    #commit and push the repo
    call("git add -A && git commit -a -m \"" + message + "\" && git push", shell=True)
    return None

  #documnet documents the repo
  def document(self):
    self.chdir('..')
    call('javadoc -d "docs" "' + self.getPackageName() + '"', shell=True)
    self.chdir('python')
    return None

  #createTest creates the test directory and makes it ready for the user
  def createTest(self):
    self.mkdir('..,test')
    self.mkdir('..,test,' + self.getLogFileDirectory())
    self.mkdir('..,test,Images')
    self.chdir('..')
    #copy current image list to the Images folder in the test directory
    filelist = [f for f in os.listdir(os.path.join(os.getcwd(), self.formatStr('Images,')))]
    for f in filelist:
      copy(os.path.join(os.getcwd(), self.formatStr('Images,') + f), os.getcwd() + self.formatStr(',test,Images'))
    self.chdir('python')
    self.__createTest = True

  #createBuild makes the build directory
  def createBuild(self):
    self.mkdir('..,build')
    self.mkdir('..,build,' + self.getLogFileDirectory())
    self.__createBuild = False

def main():
  routine = Routines()
  print 'Routine Options'
  print 'run - makes and runs the JAR file'
  print 'push - commits the current repo and pushes it'
  print 'debug - runs the current code base for testing'
  print 'clean - deletes the test and builds directories'
  rout = raw_input('Enter desired option: ')
  if(rout == 'run'):
    routine.run()
  elif(rout == 'push'):
    routine.push()
  elif(rout == 'debug'):
    routine.debug()
  elif (rout == 'clean'):
    routine.clean()
  else:
    print 'please try again'

if  __name__ =='__main__':main()
