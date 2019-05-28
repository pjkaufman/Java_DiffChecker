import os
import signal
from sys import stdin
from sys import stdout
from subprocess import call
from subprocess import check_output
from shutil import rmtree
from shutil import copy
class Routines:
  '''This class makes running routines easy and reproducible'''
  #instance variables
  __debugDir = 'build'
  __distrDir = 'build'
  __jarDir = 'lib'
  __javaCP = ''
  __resourceDir = 'resources'
  __sourceDir = 'src'
  __packageName = 'dbdiffchecker'
  __logFileDir = 'logs'
  __mainClassFile = 'DBDiffCheckerGUI'
  __testsDir = 'tests'
  testsPath = ''
  resourcePath = ''
  packagePath = ''

  def __init__(self):
    '''The constructor which initializes all instance variables'''
    #set up the class path for later use
    self.__javaCP = "\".;" + os.path.join(self.getJarPath(), '*') + "\""
    self.resourcePath = os.path.join(self.getSourceDir(), self.getResourceDir())
    self.packagePath = os.path.join(self.getSourceDir(), self.getPackageName())
    self.testsPath = os.path.join(self.getSourceDir(),  self.__testsDir)
    

  def getMainClassFile(self):
    """
    Gets the name of the file that has the main class in it in java

    Retunrs: A string which is the name of the class in java which has the main
      class in it
    """
    return self.__mainClassFile

  def getSourceDir(self):
    """
    Gets the path to the source directory which holds tests, resources, and the
    sourcecode of the application

    Retunrs: A string which is the path to the source directory
    """
    return self.__sourceDir

  def getResourceDir(self):
    """
    Gets the path to the resources directory which holds resources for the 
    application

    Retunrs: A string which is the path to the resource directory
    """
    return self.__resourceDir

  def getLogFileDirectory(self):
    """
    Gets the name of the log file directory

    Retunrs: A string which is the name of the log directory
    """
    return self.__logFileDir

  def getDebugPath(self):
    """
    Gets the path of path to the debug directory

    Retunrs: A string which is path to the debug file destination
    """
    return self.__debugDir

  def getDistrubutionPath(self):
    """
    Gets the path of the distrobution file directory

    Retunrs: A string which is the path to the distrobution file directory
    """
    return self.__distrDir

  def getJarPath(self):
    """
    Gets the path of the jar file directory

    Retunrs: A string which is path to the jar file directory
    """
    return self.__jarDir

  #getPackageName returns a string, which is the name of the package that will be acted upon by this script
  def getPackageName(self):
    """
    Gets the name of the package where all sourcecode can be found (nesting 
    included) 

    Retunrs: A string which is the package name of where the main class resides
    """
    return self.__packageName

  def getClassPath(self):
    """
    Gets the classpath for compiling java files

    Retunrs: A string which is the classpath used for compiling java files
    """
    return self.__javaCP

  
  def compile(self, path, type):
    """
    Compiles the java files and determines where to send the output

    Params: 
      path - A string which is the path to where to send the class files
      type - A boolean to determine whether or not to compile the class files 
            to the provided path
    """
    start = 'javac -Xlint:unchecked '
    if(type == True):
      start += '-d ' + path

    #compile the java files and make the jar file
    try:
      check_output(start + ' -cp ' + self.getClassPath() + ' ' + self.getPackagesToCompile(), shell=True)
      print('Compiled files')
    except Exception as e:
      print(str(e))
      print('An error occurred during compilation')
      os._exit(1) # stop the program so that the user can try to fix the issue
    return True

  def makeJar(self):
    '''Sets up the manifest and makes the JAR file'''
    self.updateManifest()
    buildDir = self.getDistrubutionPath()
    changeDirFlag = ' -C ' + self.getSourceDir() + ' '
    #compile the java files and make the jar file
    if(self.compile(self.packagePath, False)):
      call('jar cvfm ' + os.path.join(buildDir, 'Db_Diff_Checker.jar') + 
        ' manifest.mf' + changeDirFlag + self.getPackageName() + changeDirFlag
        + self.getResourceDir(), shell=True)
      self.__removeAllPackageClassFiles()

  def debug(self):
    '''Sets up the debugging environment for the current code base'''
    #create the test directory
    self.createTest()
    testDir = self.getDebugPath()
    #run compiled files with classPath
    if (self.compile(testDir, True)):
      call('java -cp ' + self.getClassPath() + ';' + os.path.join(testDir, ' ')
        + self.getPackageName() + '.' + self.getMainClassFile(), shell=True)

  def updateManifest(self):
    '''Writes to manifest.nf and sets it up for use in a JAR file'''
    #make the manfest file
    f = open('manifest.mf', 'w+')
    #add the proper verion info
    f.write("Manifest-Version: 1.0\n")
    f.write("Ant-Version: Apache Ant 1.9.7\n")
    cP = 'Class-Path: '
    mC = 'Main-Class: ' + self.getPackageName() + '.' + self.getMainClassFile()
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(), 
      self.getJarPath())) if fi.endswith('.jar')]
    for fi in filelist:
      copy(os.path.join(self.getJarPath(), fi), os.path.join(
        self.getDistrubutionPath(), os.path.join(self.getJarPath(), fi)))
      cP += os.path.join(self.getJarPath(), fi) + ' '
    #Class-Path is added if a jar file was found
    if (cP != 'Class-Path: '):
      f.write(cP  + "\n")
    f.write(mC  + "\n")
    f.close()

  def clean(self):
    '''Deletes the test, log, and build directories'''
    self.__removeDirectory(self.getDebugPath())
    self.__removeDirectory(self.getDistrubutionPath())
    self.__removeDirectory(self.getLogFileDirectory())

  def __removeDirectory(self, directory):
    """
    Removes the directory passed in if it exists

    Params: 
      directory - A string which is the directory to remove
    """
    try:
      rmtree(directory)
    except:
      pass

  def __removeAllPackageClassFiles(self):
    """
    Removes all class files from the source package by identifying all subpackages
    in the source directory
    """
    for packageDir in os.walk(self.packagePath):
      self.__removeClassFiles(os.path.join(os.getcwd(), packageDir[0]))

  def __removeClassFiles(self, directory):
    """
    Removes class files from the directory passed in

    Params: 
      directory - A string which is the directory to remove all class files from
    """
    #remove unnecesssary .class files
    filelist = [f for f in os.listdir(directory) if f.endswith('.class')]
    for f in filelist:
      os.remove(os.path.join(directory, f))

  def run(self):
    '''Creates the build directory, creates the JAR file, and runs the JAR file'''
    self.createBuild()
    self.makeJar()
    call('java -jar ' + os.path.join(self.getDistrubutionPath(), 'Db_Diff_Checker.jar'), shell=True)
    

  #push cleans, documents, and push the repo
  def push(self):
    """
    Documents the java application, asks for a commit message, and commits the
    code 
    """
    #document the repo
    self.document()
    #get necessary information from the user
    stdout.write('Commit Message: ')
    stdout.flush()
    message = stdin.readline().strip()
    #commit and push the repo
    call("git add -A && git commit -a -m \"" + message + "\" && git push", shell=True)
    

  def document(self):
    """
    Goes through and determines the subpackage flag that is to be used in order
    to document all packages for the application
    """
    packagesToDocument = '-subpackages '
    for package in os.walk(self.packagePath):
      packagesToDocument += os.path.basename(package[0]) + ':'
    call('javadoc -d "docs" -classpath ' + self.getClassPath()[:-1] + ';' + 
      self.getSourceDir() + '" ' + packagesToDocument[:-1], shell=True)
    

  def createLogs(self):
    '''Makes the log directory'''
    try:
      os.mkdir(self.getLogFileDirectory())
    except:
      pass

  def createTest(self):
    """
    Creates the test directory and makes it ready for the user by copying all
    resources into the resource folder created for the tests
    """
    debugFolder = self.getDebugPath()
    try:
       os.mkdir(debugFolder)
    except:
      pass
    self.createLogs()
    try:
       os.mkdir(os.path.join(debugFolder, self.getResourceDir()))
    except:
      pass
    #copy current resources to the resources folder in the test directory
    filelist = [f for f in os.listdir(os.path.join(os.getcwd(), self.resourcePath))]
    for f in filelist:
      copy(os.path.join(os.getcwd(), self.resourcePath, f), os.path.join(
        os.getcwd(), debugFolder, self.getResourceDir()))

  def createBuild(self):
    '''Makes the build directory with a lib folder'''
    buildFolder = self.getDistrubutionPath()
    try:
      os.mkdir(buildFolder)
    except:
      pass
    try:
      os.mkdir(os.path.join(buildFolder, self.getJarPath()))
    except:
      pass
    self.createLogs()

  def test(self):
    '''Runs all of the tests in the tests directory'''
    #remove log folder to keep tests from failing erroneously
    self.__removeDirectory(self.getLogFileDirectory())
    #create log folder for testing purposes
    self.createLogs()
    classPath = '-cp ' + self.getClassPath()[:-1] + ';' + self.getSourceDir()
    #run test
    call('javac ' + classPath + '" ' + os.path.join(self.testsPath, '*.java'))
    call('java ' + classPath + ';' + self.testsPath + '" TestRunner ')
    # remove all class files from the package directories
    self.__removeAllPackageClassFiles()
    self.__removeClassFiles(os.path.join(os.getcwd(), self.testsPath))
    self.__removeDirectory(self.getLogFileDirectory())

  def getPackagesToCompile(self):
    """
    Lists all packages that need to be compiled based on whether they are in
    the the package source directory

    Returns: A string of package compilation statements for the javac command
    """
    packageList = ''
    for x in os.walk(self.packagePath):
      packageList += os.path.join(x[0], '*.java') + ' '
    return packageList
  
def sigint_handler(signum, frame):
  '''Handles the user hitting ctrl + C by exiting the routine'''
  print ('Exiting program')
  os._exit(1)

def main():
  routine = Routines()
  print("Routine Options\nrun - makes and runs the JAR file\npush - commits the current repo and pushes it")
  print("debug - runs the current code base for testing\ntest - runs the unit tests on the source code")
  print('clean - deletes the test, logs, and build directories')
  stdout.write('Enter desired option: ')
  stdout.flush()
  rout = stdin.readline().strip()
  if(rout == 'run'):
    routine.run()
  elif(rout == 'push'):
    routine.push()
  elif(rout == 'debug'):
    routine.debug()
  elif (rout == 'clean'):
    routine.clean()
  elif (rout == 'test'):
    routine.test()
  else:
    print('please try again')

if  __name__ =='__main__':
  signal.signal(signal.SIGINT, sigint_handler)
  main()
