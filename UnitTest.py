#!/usr/bin/env python

import unittest
import os
import subprocess
from subprocess import check_output

def writeIntoAFile(filename,string):
    f = open(filename,'w')
    f.write(string)
    f.close()

class FileSystemTest(unittest.TestCase):
    
    def test_01_SimpleEcho(self):
        writeIntoAFile("hello.txt","hello")
        result1 = check_output(['cat','hello.txt'])
        x= self.assertEqual(result1,"hello",msg="simple write did not work")
          
    def test_02_SimpleFileCreation(self):
        subprocess.call(["touch","a.txt"])
        
    def test_03_SimpleDirectoryCreation(self):
        os.mkdir("dir1")
        
    def test_04_SimpleRemoveFile(self):
        os.remove("hello.txt")
        
    def test_05_SimpleDirRemoval(self):
        os.rmdirr("dir1")
        
    def test_06_fileAppend(self):
        pass
        
   
        
if __name__ == '__main__':
    unittest.main()
        
