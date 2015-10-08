#!/usr/bin/env python


class Heap:
    def __init__(self):
        self.list = []
        self.list.append(None)
        self.size = 0
    def swap(self,index1,index2):
        temp = self.list[index1]
        self.list[index1] = self.list[index2]
        self.list[index2] = temp
    def check(self,index):
        if (self.list[index] < self.list[index/2] or index==1):
            return
        else:
            self.swap(index,index/2)
            self.check(index/2)
    def insert(self,value):
        self.size+=1
        self.list.append(value)
        if (self.size > 1):
            self.check(self.size)
        
    def getMax(self):
        retval = self.list[1]
        self.list[1]=self.list.pop()
        self.size-=1
        self.adjust(1)
        return retval
        
    def adjust(self,index):
        try:
            child1 = self.list[2*index]
        except:
            child1 = -9999
        try:
            child2 = self.list[2*index+1]
        except:
            child2 = -9999
        if (self.list[index] > child1 and self.list[index]>\
            child2):
            return
        elif (self.list[index] < child1 and self.list[index] < child2):
            if (child1 > child2):
                self.swap(index,2*index)
                self.adjust(2*index)
            else:
                self.swap(index,2*index+1)
                self.adjust(2*index+1)
        elif (self.list[index] < child1):
            self.swap(index,2*index)
            self.adjust(2*index)
        else:
            self.swap(index,2*index+1)
            self.adjust(2*index+1)
            
    def display(self):
        print ""
        for i in self.list:
            print i
        print ""
            
if __name__ == '__main__':
    myheap = Heap()
    myheap.insert(10)
    myheap.insert(20)
    myheap.insert(5)
    myheap.insert(30)
   
    myheap.display()
    
        
    print(myheap.getMax())
    
    myheap.display()
        
        
