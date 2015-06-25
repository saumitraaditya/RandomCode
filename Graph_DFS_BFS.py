#!usr/bin/python

from collections import deque

class adjListNode(object):
    def __init__(self,name,NeighbourList = None):
        self.name = name
        self.visited = False
        self.NeighbourList = []
        
    def addNeighbour(self,neighbour):
        self.NeighbourList.append(neighbour)
        
class adjList(object):
    def __init__(self):
        self.myList = []
        
    def addNode(self,name):
        newnode = adjListNode(name)
        self.myList.append(newnode)
        
    def delNode(self,name):
        index = 0
        for node in self.myList:
            if (node.name==name):
                break
            else:
                index = index+1
        self.myList.pop(index)
        
    def addEdge(self,src,dst):
        for i in self.myList:
            if (i.name == src):
                i.addNeighbour(dst)
            elif (i.name == dst):
                i.addNeighbour(src)
                
    def retNode(self,node_name):
        for i in self.myList:
            if (i.name == node_name):
                return i
        return None
                
    def printList(self):
        for each in self.myList:
            print "NodeName: %s \t Neighbours %s"%(each.name,each.NeighbourList)
            print ('\n')
        
        
class Graph(object):
    def __init__(self,size = 0):
        self.aList = adjList()
        for i in range(1,size+1):
            self.aList.addNode(i)
        
    def addEdge(self,src,dst):
        self.aList.addEdge(src,dst)
                
    def printGraph(self):
        self.aList.printList()
        
    def returnNode(self,name):
        return self.aList.retNode(name)
        
class DFS(object):
    def __init__(self,graph):
        self.stack = []
        
         
    def findUnvisitedNeighbour(self,name):
        tempNode = graph.returnNode(name)
        if tempNode == None:
            return None
        for node in tempNode.NeighbourList:
            tNode = graph.returnNode(node)
            if (tNode.visited == False):
                return tNode
        return None
        
    def traverse(self,root):
        rootNode = graph.returnNode(root)
        self.stack.append(rootNode)
        print ("Inserted node %s \n"%(rootNode.name))
        rootNode.visited = True
        while (len(self.stack)!=0):
            topnode = self.stack[len(self.stack)-1]
            Tnode = self.findUnvisitedNeighbour(topnode.name)
            if (Tnode != None):
                self.stack.append(Tnode)
                print ("Inserted node %s \n"%(Tnode.name))
                Tnode.visited = True
                
            else:
                self.stack.pop()
                
class BFS(object):
    def __init__(self,graph):
        self.queue = deque()
        self.graph = graph
        
    def findUnvisitedNeighbour(self,name):
        tempNode = self.graph.returnNode(name)
        if tempNode == None:
            return None
        for node in tempNode.NeighbourList:
            tNode = graph.returnNode(node)
            if (tNode.visited == False):
                return tNode
        return None
        
    def traverse(self,root):
        rootNode = self.graph.returnNode(root)
        self.queue.append(rootNode)
        print ("Inserted node %s \n"%(rootNode.name))
        rootNode.visited = True
        while (len(self.queue)!=0):
            topnode = self.queue[0]
            Tnode = self.findUnvisitedNeighbour(topnode.name)
            if (Tnode != None):
                self.queue.append(Tnode)
                print ("Inserted node %s \n"%(Tnode.name))
                Tnode.visited = True
            else:
                self.queue.popleft()
            
if __name__ == '__main__':
    graph = Graph(size = 6)
    graph.addEdge(1,5)
    graph.addEdge(1,6)
    graph.addEdge(2,1)
    graph.addEdge(1,3)
    graph.addEdge(3,4)
    graph.addEdge(3,5)
    graph.addEdge(2,4)
    
    graph.printGraph()
    
    #dfs = DFS(graph)
    #dfs.traverse(1)
    
    bfs = BFS(graph)
    bfs.traverse(1)
    
    
    
