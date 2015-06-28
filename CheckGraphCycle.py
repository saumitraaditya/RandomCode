#!usr/bin/python

# Demonstration of Union-Find on a undirectional graph to
# detect a cycle in the graph.

class edge(object):

    def __init__(self,src,dst):
        self.src = src
        self.dst = dst
        
class Graph(object):
    def __init__(self,v,e):
        self.v = v
        self.e = e
        self.edge_list = []
        
class utility(object):

    def __init__(self):
        self.parent_list = []
        
    def find(self,node):
        if self.parent_list[node] == -1:
            return node
        else:
            return self.find(self.parent_list[node])
            
    def union(self,x,y):
        xset = self.find(x)
        yset = self.find(y)
        self.parent_list[xset] = yset
        
    def check_cycle(self,graph):
        for i in range(0,graph.v):
            self.parent_list.append(-1)
        for each_edge in graph.edge_list:
            x = self.find(each_edge.src)
            y = self.find(each_edge.dst)
            
            if (x == y):
                print ("edge resulting in cycle is %s <-> %s "%(each_edge.src,each_edge.dst))
                return 1
            self.union(x,y)
        return 0
        
if __name__ == '__main__':
    
    graph = Graph(4,3)
    
    # add edges to the graph
    graph.edge_list.append(edge(0,1))
    graph.edge_list.append(edge(1,3))
    graph.edge_list.append(edge(0,2))
    #graph.edge_list.append(edge(3,2))
    
    utl_obj = utility()
    
    if utl_obj.check_cycle(graph) == 1:
        print ("Cycle exists in the Graph.")
    else:
        print (" No cycle in the Graph.")
        
    
