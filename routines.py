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
  __directories = {'debug': 'build', 'distrobution':'build', 'jar': 'lib', 
    'resource': 'resources', 'source': 'src', 'test': 'tests', 'log': 'logs'}
  __compileFlags = {'cp': None, 'C': None, 'main': 'DBDiffCheckerGUI', 
    'package': 'dbdiffchecker' }
  __paths = {'test': None, 'resource': None, 'package': None}

  def __init__(self):
    '''The constructor which initializes all instance variables'''
    #set up compile flags for later use
    self.__compileFlags['cp'] = " -cp \".;" + os.path.join(self.getDirectoryName('jar'), '*') + "\""
    self.__compileFlags['C'] = ' -C ' + self.getDirectoryName('source') + ' '
    # set up paths for later use
    self.__paths['resource'] = os.path.join(self.getDirectoryName('source'), self.getDirectoryName('resource'))
    self.__paths['package'] = os.path.join(self.getDirectoryName('source'), self.getFlag('package'))
    self.__paths['test'] = os.path.join(self.getDirectoryName('source'),  self.getDirectoryName('test'))
    
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

  def __removeAllClassFiles(self):
    """
    Removes all class files from the source folders by identifying all subpackages
    in the source directory and then removing all class files
    """
    for dir in os.walk(self.getDirectoryName('source')):
      directory = os.path.join(os.getcwd(), dir[0]) 
      filelist = [f for f in os.listdir(directory) if f.endswith('.class')]
      for f in filelist:
        os.remove(os.path.join(directory, f))

  def getDirectoryName(self, dirType):
    """
    Gets the name of a directory based on the directory type requested
    Params:
      dirType - A string which is the key for the directory to retrieve from 
        the directories dictionary
    Retunrs: A string which is the name of a directory which is determined by 
      dirType
    """
    return self.__directories[dirType]

  def getPath(self, pathType):
    """
    Gets the path of a directory based on the path type
    Params:
      pathType - A string which is the key for the path to retrieve from 
        the paths dictionary
    Retunrs: A string which is the path of a directory which is determined by 
      pathType
    """
    return self.__paths[pathType]

  def getFlag(self, flagType):
    """
    Gets the flag for the java command based on the flagType
    Params:
      flagType - A string which is the key for the flag to retrieve from 
        the flags dictionary
    Retunrs: A string which is the flag of a java command which is determined by
      flagType
    """
    return self.__compileFlags[flagType]
  
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
      check_output(start + self.getFlag('cp') + ' ' + self.getPackagesToCompile(), shell=True)
      print('Compiled files')
    except Exception as e:
      print(str(e))
      print('An error occurred during compilation')
      os._exit(1) # stop the program so that the user can try to fix the issue
    return True

  def makeJar(self):
    '''Sets up the manifest and makes the JAR file'''
    self.updateManifest()
    buildDir = self.getDirectoryName('distrobution')
    #compile the java files and make the jar file
    if(self.compile(self.getFlag('package'), False)):
      call('jar cvfm ' + os.path.join(buildDir, 'Db_Diff_Checker.jar') + 
        ' manifest.mf' + self.getFlag('C') + self.getFlag('package') +
        self.getFlag('C') + self.getDirectoryName('resource'), shell=True)
      self.__removeAllClassFiles()

  def debug(self):
    '''Sets up the debugging environment for the current code base'''
    #create the test directory
    self.createTest()
    testDir = self.getDirectoryName('debug')
    #run compiled files with classPath
    if (self.compile(testDir, True)):
      call('java' + self.getFlag('cp') + ';' + os.path.join(testDir, ' ')
        + self.getFlag('package') + '.' + self.getFlag('main'), shell=True)

  def updateManifest(self):
    '''Writes to manifest.nf and sets it up for use in a JAR file'''
    #make the manfest file
    f = open('manifest.mf', 'w+')
    #add the proper verion info
    f.write("Manifest-Version: 1.0\n")
    f.write("Ant-Version: Apache Ant 1.9.7\n")
    cP = 'Class-Path: '
    mC = 'Main-Class: ' + self.getFlag('package') + '.' + self.getFlag('main')
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(), 
      self.getDirectoryName('jar'))) if fi.endswith('.jar')]
    for fi in filelist:
      copy(os.path.join(self.getDirectoryName('jar'), fi), os.path.join(
        self.getDirectoryName('distrobution'), os.path.join(self.getDirectoryName('jar'), fi)))
      cP += os.path.join(self.getDirectoryName('jar'), fi) + ' '
    #Class-Path is added if a jar file was found
    if (cP != 'Class-Path: '):
      f.write(cP  + "\n")
    f.write(mC  + "\n")
    f.close()

  def clean(self):
    '''Deletes the test, log, and build directories'''
    self.__removeDirectory(self.getDirectoryName('debug'))
    self.__removeDirectory(self.getDirectoryName('distrobution'))
    self.__removeDirectory(self.getDirectoryName('log'))

  def run(self):
    '''Creates the build directory, creates the JAR file, and runs the JAR file'''
    self.createBuild()
    self.makeJar()
    call('java -jar ' + os.path.join(self.getDirectoryName('distrobution'), 'Db_Diff_Checker.jar'), shell=True)
    
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
    for package in os.walk(self.getPath('package')):
      packagesToDocument += os.path.basename(package[0]) + ':'
    call('javadoc -d "docs"' + self.getFlag('cp')[:-1] + ';' + 
      self.getDirectoryName('source') + '" ' + packagesToDocument[:-1], shell=True)
    
  def createLogs(self):
    '''Makes the log directory'''
    try:
      os.mkdir(self.getDirectoryName('log'))
    except:
      pass

  def createTest(self):
    """
    Creates the test directory and makes it ready for the user by copying all
    resources into the resource folder created for the tests
    """
    debugFolder = self.getDirectoryName('debug')
    try:
       os.mkdir(debugFolder)
    except:
      pass
    self.createLogs()
    try:
       os.mkdir(os.path.join(debugFolder, self.getDirectoryName('resource')))
    except:
      pass
    #copy current resources to the resources folder in the test directory
    filelist = [f for f in os.listdir(os.path.join(os.getcwd(), self.getPath('resource')))]
    for f in filelist:
      copy(os.path.join(os.getcwd(), self.getPath('resource'), f), os.path.join(
        os.getcwd(), debugFolder, self.getDirectoryName('resource')))

  def createBuild(self):
    '''Makes the build directory with a lib folder'''
    buildFolder = self.getDirectoryName('distrobution')
    try:
      os.mkdir(buildFolder)
    except:
      pass
    try:
      os.mkdir(os.path.join(buildFolder, self.getDirectoryName('jar')))
    except:
      pass
    self.createLogs()

  def test(self):
    '''Runs all of the tests in the tests directory'''
    #remove log folder to keep tests from failing erroneously
    self.clean()
    #create log folder for testing purposes
    self.createLogs()
    classPath = self.getFlag('cp')[:-1] + ';' + self.getDirectoryName('source')
    #run test
    call('javac' + classPath + '" ' + os.path.join(self.getPath('test'), '*.java'))
    call('java' + classPath + ';' + self.getPath('test') + '" TestRunner ')
    # remove all class files from the package directories
    self.__removeAllClassFiles()
    self.clean()

  def getPackagesToCompile(self):
    """
    Lists all packages that need to be compiled based on whether they are in
    the the package source directory
    Returns: A string of package compilation statements for the javac command
    """
    packageList = ''
    for x in os.walk(self.getPath('package')):
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
