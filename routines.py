import os
from subprocess import call
from subprocess import check_output
from shutil import rmtree
from shutil import copy
class Routines:
  #instance variables
  __debugDir = 'test'
  __distrDir = 'build'
  __jarDir = 'jarLibrary'
  __javaCP = ''
  __packageName = 'dbdiffchecker'
  __distrJarDir = 'lib'
  __logFileDir = 'logs'

  #__init__ is the constructor which initializes all instance variables
  def __init__(self):
    #sets the class path for later use
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(), self.getJarPath())) if fi.endswith('.jar')]
    for fi in filelist:
      self.__javaCP += "\".;" + os.path.join(self.getJarPath(), fi) + "\";"
    self.__javaCP = self.__javaCP[:-1]
    return None

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

  #getClassPath is the classpath for java compilation
  def getClassPath(self):
    return self.__javaCP

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
      check_output(start + ' -cp ' + self.getClassPath() + ' ' + os.path.join(self.getPackageName(), '*.java'), shell=True)
      print 'Compiled files'
    except Exception as e:
      print str(e)
      print 'An error occurred during compilation'
      os._exit(1) # stop the program so that the user can try to fix the issue
    return True

  #makeJar sets up the manifest and makes the JAR file
  def makeJar(self):
    self.updateManifest()
    logDir = self.getLogFileDirectory()
    buildDir = self.getDistrubutionPath()
    packageName = self.getPackageName()
    #compile the java files and make the jar file
    if(self.compile(packageName, False)):
      call('jar cvfm ' + os.path.join(buildDir, 'Db_Diff_Checker.jar') + ' manifest.mf ' +
          packageName + ' Images ' + self.getJarPath() + ' ' + os.path.join(buildDir, logDir), shell=True)
      #remove unnecesssary directory
      self.__removeDirectory(os.path.join(buildDir, logDir))
      #remove unnecesssary .class files
      filelist = [f for f in os.listdir(os.path.join(os.getcwd(), self.getPackageName())) if f.endswith('.class')]
      for f in filelist:
        os.remove(os.path.join(os.getcwd(), packageName, f))

    return None

  #debug sets up a debugging environment for the current code base
  def debug(self):
    #create the test directory
    self.createTest()
    testDir = self.getDebugPath()
    #run compiled files with classPath
    if (self.compile(testDir, True)):
      call('java -cp ' + os.path.join(testDir, ' ') + self.getPackageName() + '.DB_Diff_Checker_GUI', shell=True) #needs an update...

    return None

  #updateManifest writes to manifest.mf and sets it up for use in a JAR file
  def updateManifest(self):
    #make the manfest file
    f = open('manifest.mf', 'w+')
    #add the proper verion info
    f.write("Manifest-Version: 1.0\n")
    f.write("Ant-Version: Apache Ant 1.9.7\n")
    cP = 'Class-Path: '
    mC = 'Main-Class: ' + self.getPackageName() + '.DB_Diff_Checker_GUI'
    #get current jar list and add it to the Class-Path
    filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(), self.getJarPath())) if fi.endswith('.jar')]
    for fi in filelist:
      cP += os.path.join(self.getDistributionJarDirectory(), fi) + ' '
    #Class-Path is added if a jar file was found
    if (cP != 'Class-Path: '):
      f.write(cP  + "\n")
    f.write(mC  + "\n")
    f.close()
    return None

  #clean deletes the test and build directories
  def clean(self):
    self.__removeDirectory(self.getDebugPath())
    self.__removeDirectory(self.getDistrubutionPath())

  #__removeDirectory takes in a directory and removes it if it exists
  #param: directory is the directory to remove
  def __removeDirectory(self, directory):
    try:
      rmtree(directory)
    except:
      pass
    return None

  #run makes and runs the JAR file
  def run(self):
    self.createBuild()
    self.makeJar()
    call('java -jar ' + os.path.join(self.getDistrubutionPath(), 'Db_Diff_Checker.jar'), shell=True)
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
    call('javadoc -d "docs" "' + self.getPackageName() + '"', shell=True)
    return None

  #createTest creates the test directory and makes it ready for the user
  def createTest(self):
    debugFolder = self.getDebugPath()
    try:
       os.mkdir(debugFolder)
    except:
      pass
    try:
       os.mkdir(os.path.join(debugFolder, self.getLogFileDirectory()))
    except:
      pass
    try:
       os.mkdir(os.path.join(debugFolder, 'Images'))
    except:
      pass

    #copy current image list to the Images folder in the test directory
    filelist = [f for f in os.listdir(os.path.join(os.getcwd(),'Images'))]
    for f in filelist:
      copy(os.path.join(os.getcwd(), 'Images', f), os.path.join(os.getcwd(), debugFolder, 'Images'))

  #createBuild makes the build directory
  def createBuild(self):
    buildFolder = self.getDistrubutionPath()
    try:
      os.mkdir(buildFolder)
    except:
      pass
    try:
       os.mkdir(os.path.join(buildFolder,  self.getLogFileDirectory()))
    except:
      pass

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
