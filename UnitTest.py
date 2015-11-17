#!/usr/bin/env python

import unittest
import os
import subprocess
from subprocess import check_output


'''
Test-1
hierarchial directory creation--- fusemount/dir1/dir2/dir3
mkdir dir1;cddir dir1;mkdir dir2;chdir dir2;mkdir dir3;chdir dir3
list dir1, list dir2 check for correctness
Test-2
Creation of files in dir2 & dir3
create f21.txt,f22.txt & f31.txt,f32.txt
list dir2, list dir3 check for correctness
Test 3
Read files f21.txt , f22.txt
Test 4
Remove dir3
list dir2--check
Test 5- remove mile
rm f22.txt--list dir2 -- check
Test 6
mv dir2/f22.txt ../f11.txt
read dir1/f11.txt--check
'''

def writeIntoAFile(filename,string):
    f = open(filename,'w')
    f.write(string)
    f.close()
    
def readFile(filename):
    f = open(filename,'r')
    ret_string = f.read(3)
    f.close()
    return ret_string

class FileSystemTest(unittest.TestCase):
           
    def test_01_NestedDirectories(self):
        os.chdir(basepath)
        os.chdir("fusemount")
        os.mkdir("dir1")
        os.chdir("dir1")
        os.mkdir("dir2")
        os.chdir("dir2")
        os.mkdir("dir3")
        os.chdir("dir3")
        self.assertEqual(os.getcwd(),"/dir1/dir2/dir3","Nested directory creation failed")
        
    def test_02_NestedFileCreation(self):
        os.chdir(basepath)
        os.chdir("fusemount")
        os.chdir("dir1/dir2")
        writeIntoAFile("f21.txt","f21")
        writeIntoAFile("f22.txt","f22")
        contents1 = os.listdir(".")
        self.assertEqual(contents1,[],"Nested File creation failed")
        
    def test_03_ReadFiles(self):
        os.chdir(basepath)
        os.chdir("fusemount/dir1/dir2")
        ret_val = readFile("f22.txt")
        self.assertEqual(ret_val,"f22","Read test on files failed")
        
    def test_04_removeFile(self):
        os.chdir(basepath)
        os.chdir("fusemount/dir1/dir2/")
        os.remove("f22.txt")
        contents = os.listdir(".")
        self.assertEqual(contents,[],"File deletion failed")
        
    def test_05_removeDir(self):
        os.chdir(basepath)
        os.chdir("fusemount/dir1/dir2/")
        os.rmdir("dir3")
        contents = os.listdir(".")
        self.assertEqual(contents,[],"Directory deletion failed")
        
    def test_06_renameFile(self):
        os.chdir(basepath)
        os.chdir("fusemount/dir1/dir2/")
        os.rename("f21.txt","../../f11.txt")
        os.chdir("../..")
        contents = os.listdir(".")
        self.assertEqual(contents,[],"File renaming failed")
        
        
        
   
        
if __name__ == '__main__':
    unittest.main()
        
