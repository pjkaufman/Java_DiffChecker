import os
import signal
import platform
from sys import stdin
from sys import stdout
from subprocess import call
from subprocess import check_output
from shutil import rmtree
from shutil import copy

dirs = {'debug': 'build', 'dist':'bin', 'jar': 'lib', 'res': 'resources',
  'source': 'src', 'test': 'test', 'log': 'log'}
libPath = os.path.join(dirs['jar'], '*')
package = 'dbdiffchecker'
mainClass = 'DBDiffCheckerGUI'
osName = platform.system()

def get_java_seperator():
    if osName == 'Windows':
      return ';'
    return ':'

def compile_files(path, type):
  start = 'javac -Xlint:unchecked '
  if(type == True):
    start += '-d ' + path
  # get all subpackages inside of the package
  packageList = ''
  for x in os.walk(os.path.join(dirs['source'], package)):
    packageList += os.path.join(x[0], '*.java') + ' '
  #compile the java files
  try:
    check_output(start + ' -cp "' + libPath + '" ' + packageList, shell=True)
    print('Compiled files')
  except Exception as e:
    print(str(e))
    print('An error occurred during compilation')
    os._exit(1) # stop the program so that the user can try to fix the issue
  return True

def remove_all_class_files():
  for dir in os.walk(dirs['source']):
    directory = os.path.join(os.getcwd(), dir[0])
    filelist = [f for f in os.listdir(directory) if f.endswith('.class')]
    for f in filelist:
      os.remove(os.path.join(directory, f))

def setup_debug_env():
  try:
    os.mkdir(dirs['debug'])
  except:
    pass
  try:
    os.mkdir(os.path.join(dirs['debug'], dirs['res']))
  except:
    pass
  create_log_dir()
  #copy current resources to the resources folder in the test directory
  filelist = [f for f in os.listdir(os.path.join(os.getcwd(), dirs['source'], dirs['res']))]
  for f in filelist:
    copy(os.path.join(os.getcwd(), dirs['source'], dirs['res'], f), os.path.join(
      os.getcwd(), dirs['debug'], dirs['res']))

def clean():
  rmtree(dirs['debug'], True)
  rmtree(dirs['dist'], True)
  rmtree(dirs['log'], True)

def update_manifest():
  f = open('manifest.mf', 'w+')
  # add the proper verion info
  f.write("Manifest-Version: 1.0\n")
  cP = 'Class-Path: '
  mC = 'Main-Class: ' + package + '.' + mainClass
  # get current jar list and add it to the Class-Path
  filelist = [fi for fi in os.listdir(os.path.join(os.getcwd(),
    dirs['jar'])) if fi.endswith('.jar')]
  for fi in filelist:
    copy(os.path.join(dirs['jar'], fi), os.path.join(dirs['dist'], dirs['jar'], fi))
    cP += os.path.join(dirs['jar'], fi) + ' '
  # Class-Path is added if a jar file was found
  if (cP != 'Class-Path: '):
    f.write(cP  + "\n")
  f.write(mC  + "\n")
  f.close()

def setup_run_env():
  try:
    os.mkdir(dirs['dist'])
  except:
    pass
  try:
    os.mkdir(os.path.join(dirs['dist'], dirs['jar']))
  except:
    pass
  create_log_dir()

def push():
  packagesToDocument = '-subpackages '
  for packageName in os.walk(os.path.join(dirs['source'], package)):
    packagesToDocument += os.path.basename(packageName[0]) + ':'
  call('javadoc -d "docs"' + ' -cp "' + libPath + get_java_seperator() + dirs['source']
    + get_java_seperator() + os.path.join(dirs['source'], package) + '" ' +
    packagesToDocument[:-1], shell=True)
  stdout.write('Commit Message: ')
  stdout.flush()
  message = stdin.readline().strip()
  call('git add -A && git commit -a -m "' + message + '" && git push', shell=True)

def create_log_dir():
  try:
    os.mkdir(dirs['log'])
  except:
    pass

def setup_test_env():
  clean()
  create_log_dir()

def sigint_handler(signum, frame):
  '''Handles the user hitting ctrl + C by exiting the routine'''
  print ('Exiting program')
  os._exit(1)

def main():
  print("Routine Options")
  print("run - makes and runs the JAR file")
  print("push - commits the current repo and pushes it")
  print("debug - runs the current code base for testing")
  print("test - runs the unit tests on the source code")
  print('clean - deletes the test, logs, and build directories')
  stdout.write('Enter desired option: ')
  stdout.flush()
  routine = stdin.readline().strip()
  if(routine == 'run'):
    setup_run_env()
    update_manifest()
    if(compile_files(package, False)):
      call('jar cvfm ' + os.path.join(dirs['dist'], 'Db_Diff_Checker.jar') + ' manifest.mf'
        + ' -C ' + dirs['source'] + ' ' + package + ' -C ' + dirs['source'] + ' ' + dirs['res'],
        shell=True)
      remove_all_class_files()
      call('java -jar ' + os.path.join(dirs['dist'], 'Db_Diff_Checker.jar'), shell=True)
  elif(routine == 'push'):
    push()
  elif(routine == 'debug'):
    setup_debug_env()
    if (compile_files(dirs['debug'], True)):
      print('java -cp ' + libPath + get_java_seperator() + os.path.join(dirs['debug'], ' ') + package + '.' + mainClass)
      call('java -cp ' + libPath + get_java_seperator() + os.path.join(dirs['debug'], ' ') + package + '.' + mainClass, shell=True)
  elif (routine == 'clean'):
    clean()
  elif (routine == 'test'):
    setup_test_env()
    call('javac -cp "' + libPath + get_java_seperator() + dirs['source'] + '" ' +
      os.path.join(dirs['source'], dirs['test'], '*.java'), shell=True)
    call('java -cp "' + libPath + get_java_seperator() + dirs['source'] + get_java_seperator() + dirs['test'] + '" '
      + dirs['test'] + '.TestRunner', shell=True)
    remove_all_class_files()
    clean()
  else:
    print('please try again')

if  __name__ =='__main__':
  signal.signal(signal.SIGINT, sigint_handler)
  main()
