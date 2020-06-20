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
    'resource': 'resources', 'source': 'src', 'test': 'test', 'log': 'log'}
  __compileFlags = {'cp': None, 'C': None, 'main': 'DBDiffCheckerGUI',
    'package': 'dbdiffchecker' }
  __paths = {'test': None, 'resource': None, 'package': None}

  def __init__(self):
    '''The constructor which initializes all instance variables'''
    #set up compile flags for later use
    self.__compileFlags['cp'] = " -cp \".;" + os.path.join(self.__getDirectoryName('jar'), '*') + "\""
    self.__compileFlags['C'] = ' -C ' + self.__getDirectoryName('source') + ' '
    # set up paths for later use
    self.__paths['resource'] = os.path.join(self.__getDirectoryName('source'), self.__getDirectoryName('resource'))
    self.__paths['package'] = os.path.join(self.__getDirectoryName('source'), self.__getFlag('package'))
    self.__paths['test'] = os.path.join(self.__getDirectoryName('source'),  self.__getDirectoryName('test'))

  def __removeDirectory(self, directory):
    """Removes the directory passed in if it exists
    Params:
      directory - A string which is the directory to remove
    """
    try:
      rmtree(directory)
    except:
      pass

  def __removeAllClassFiles(self):
    """Removes all class files from the source folders by identifying all
    subpackages in the source directory and then removing all class files
    """
    for dir in os.walk(self.__getDirectoryName('source')):
      directory = os.path.join(os.getcwd(), dir[0])
      filelist = [f for f in os.listdir(directory) if f.endswith('.class')]
      for f in filelist:
        os.remove(os.path.join(directory, f))

  def __getDirectoryName(self, dirType):
    """Gets the name of a directory based on the directory type requested
    Params:
      dirType - A string which is the key for the directory to retrieve from
        the directories dictionary
    Retunrs: A string which is the name of a directory which is determined by
      dirType
    """
    return self.__directories[dirType]

  def __getPath(self, pathType):
    """Gets the path of a directory based on the path type
    Params:
      pathType - A string which is the key for the path to retrieve from
        the paths dictionary
    Retunrs: A string which is the path of a directory which is determined by
      pathType
    """
    return self.__paths[pathType]

  def __getFlag(self, flagType):
    """Gets the flag for the java command based on the flagType
    Params:
      flagType - A string which is the key for the flag to retrieve from
        the flags dictionary
    Retunrs: A string which is the flag of a java command which is determined by
      flagType
    """
    return self.__compileFlags[flagType]

  def __compile(self, path, type):
    """Compiles the java files and determines where to send the output
    Params:
      path - A string which is the path to where to send the class files
      type - A boolean to determine whether or not to compile the class files
            to the provided path
    """
    start = 'javac -Xlint:unchecked '
    if(type == True):
      start += '-d ' + path
    # get all subpackages inside of the package
    packageList = ''
    for x in os.walk(self.__getPath('package')):
      packageList += os.path.join(x[0], '*.java') + ' '

    #compile the java files and make the jar file
    try:
      check_output(start + self.__getFlag('cp') + ' ' + packageList, shell=True)
      print('Compiled files')
    except Exception as e:
      print(str(e))
      print('An error occurred during compilation')
      os._exit(1) # stop the program so that the user can try to fix the issue
    return True

  def __setupEnvironment(self, env):
    """Sets up the desired environment based on the provided environment name
    Note: the logs folder will always be created if it does not already exist
    Params:
      env - A string which is the name of the environment to set up for
    """
    if(env == 'debug'):
      debugFolder = self.__getDirectoryName('debug')
      try:
        os.mkdir(debugFolder)
      except:
        pass
      try:
        os.mkdir(os.path.join(debugFolder, self.__getDirectoryName('resource')))
      except:
        pass
      #copy current resources to the resources folder in the test directory
      filelist = [f for f in os.listdir(os.path.join(os.getcwd(), self.__getPath('resource')))]
      for f in filelist:
        copy(os.path.join(os.getcwd(), self.__getPath('resource'), f), os.path.join(
          os.getcwd(), debugFolder, self.__getDirectoryName('resource')))
    elif(env == 'distrobution'):
      buildFolder = self.__getDirectoryName('distrobution')
      try:
        os.mkdir(buildFolder)
      except:
        pass
      try:
        os.mkdir(os.path.join(buildFolder, self.__getDirectoryName('jar')))
      except:
        pass
    # always create logs folder
    try:
      os.mkdir(self.__getDirectoryName('log'))
    except:
      pass

  def debug(self):
    '''Sets up the debugging environment for the current code base'''
    #create the test directory
    self.__setupEnvironment('debug')
    testDir = self.__getDirectoryName('debug')
    #run compiled files with classPath
    if (self.__compile(testDir, True)):
      call('java' + self.__getFlag('cp') + ';' + os.path.join(testDir, ' ')
        + self.__getFlag('package') + '.' + self.__getFlag('main'), shell=True)

  def clean(self):
    '''Deletes the test, log, and build directories'''
    self.__removeDirectory(self.__getDirectoryName('debug'))
    self.__removeDirectory(self.__getDirectoryName('distrobution'))
    self.__removeDirectory(self.__getDirectoryName('log'))

  def run(self):
    '''Creates the build directory, creates the JAR file, and runs the JAR file'''
    self.__setupEnvironment('distrobution')
    # make the manfest file
    f = open('manifest.mf', 'w+')
    # add the proper verion info
    f.write("Manifest-Version: 1.0\n")
    cP = 'Class-Path: '
    mC = 'Main-Class: ' + self.__getFlag('package') + '.' + self.__getFlag('main')
    # get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(),
      self.__getDirectoryName('jar'))) if fi.endswith('.jar')]
    for fi in filelist:
      copy(os.path.join(self.__getDirectoryName('jar'), fi), os.path.join(
        self.__getDirectoryName('distrobution'), os.path.join(self.__getDirectoryName('jar'), fi)))
      cP += os.path.join(self.__getDirectoryName('jar'), fi) + ' '
    # Class-Path is added if a jar file was found
    if (cP != 'Class-Path: '):
      f.write(cP  + "\n")
    f.write(mC  + "\n")
    f.close()
    buildDir = self.__getDirectoryName('distrobution')
    #compile the java files and make the jar file
    if(self.__compile(self.__getFlag('package'), False)):
      call('jar cvfm ' + os.path.join(buildDir, 'Db_Diff_Checker.jar') +
        ' manifest.mf' + self.__getFlag('C') + self.__getFlag('package') +
        self.__getFlag('C') + self.__getDirectoryName('resource'), shell=True)
      self.__removeAllClassFiles()
    # run jar file
    call('java -jar ' + os.path.join(self.__getDirectoryName('distrobution'), 'Db_Diff_Checker.jar'), shell=True)

  def push(self):
    """Documents the java application, asks for a commit message, and commits
    the code
    """
    #document the repo
    packagesToDocument = '-subpackages '
    for package in os.walk(self.__getPath('package')):
      packagesToDocument += os.path.basename(package[0]) + ':'
    call('javadoc -d "docs"' + self.__getFlag('cp')[:-1] + ';' +
      self.__getDirectoryName('source') + '" ' + packagesToDocument[:-1], shell=True)
    #get necessary information from the user
    stdout.write('Commit Message: ')
    stdout.flush()
    message = stdin.readline().strip()
    #commit and push the repo
    call("git add -A && git commit -a -m \"" + message + "\" && git push", shell=True)

  def test(self):
    '''Runs all of the tests in the tests directory'''
    #remove log folder to keep tests from failing erroneously
    self.clean()
    #create log folder for testing purposes
    self.__setupEnvironment('')
    classPath = self.__getFlag('cp')[:-1] + ';' + self.__getDirectoryName('source')
    #run test
    call('javac' + classPath + '" ' + os.path.join(self.__getPath('test'), '*.java'))
    call('java' + classPath + ';' + self.__getPath('test') + '" test.TestRunner ')
    # remove all class files from the package directories
    self.__removeAllClassFiles()
    self.clean()

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
