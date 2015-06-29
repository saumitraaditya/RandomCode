#!usr/bin/python

class edge(object):

    def __init__(self,v,e,weight):
        self.src = v
        self.dst = e
        self.weight = weight
        
class Graph(object):
    def __init__(self,nV,nE):
        self.edge_list = []
        self.nV = nV
        self.nE = nE
        
class subset(object):
    def __init__(self,parent,rank):
        self.parent = parent
        self.rank = rank
        
        
class utility(object):
    def __init__(self):
        self.subset_set = []
        
    def find(self,node):
        if (self.subset_set[node].parent != node):
            # find root and make root a parent of subtree rooted at node.
            self.subset_set[node].parent = self.find(self.subset_set[node].parent)
        return self.subset_set[node].parent
        
    def union(self,x,y):
        xroot = self.find(x)
        yroot = self.find(y)
        # put smaller ranked tree as subtree of higher ranked tree.
        if (self.subset_set[xroot].rank < self.subset_set[yroot].rank):
            self.subset_set[xroot].parent= yroot
        elif (self.subset_set[yroot].rank < self.subset_set[xroot].rank):
             self.subset_set[yroot].parent= xroot
        else:
            # if ranks of both subtrees are same than make one as parent of another and increment the rank of parent subtree root.
            self.subset_set[yroot].parent= xroot
            self.subset_set[xroot].rank = self.subset_set[xroot].rank + 1
            
    def check_cycle(self,graph):
        # initially there are as many subsets as the number of vertices, and each vertice is a parent of itself with a rank of 0.
        for i in range(0,graph.nV):
            self.subset_set.append(subset(i,0))
        for each_edge in graph.edge_list:
            x = self.find(each_edge.src)
            y = self.find(each_edge.dst)
            
            if (x == y):
                print ("edge resulting in cycle is %s <-> %s "%(each_edge.src,each_edge.dst))
                return 1
            self.union(x,y)
        return 0
        
    def kruskal(self,graph):
        result_edges = []
        index = 0
        e_index = 0
        sorted_edge_list = sorted(graph.edge_list,key = lambda x:x.weight, reverse=False)
        print ("sorted edge list is %s "%(sorted_edge_list))
        for i in range(0,graph.nV):
            self.subset_set.append(subset(i,0))
        while ((e_index < graph.nV -1)):
            # pick the smallest edge, continue till we have V-1 edges
            temp_edge = sorted_edge_list[index]
            index+=1
            print ("index %s"%(index))
            x = self.find(temp_edge.src)
            y = self.find(temp_edge.dst)
            if (x != y):
                result_edges.append(temp_edge)
                e_index +=1
                self.union(x,y)
                
        print("Edges in resulting MST are ")
        for each in result_edges:
            print ("src %s dst %s weight %s"%(each.src,each.dst,each.weight))
        
    def compareWeights(self,a,b):
        return a.weight > b.weight
        
if __name__ == '__main__':
    
    graph = Graph(4,5)
    
    # add edges to the graph
    graph.edge_list.append(edge(0,1,10))
    graph.edge_list.append(edge(0,3,5))
    graph.edge_list.append(edge(0,2,6))
    graph.edge_list.append(edge(1,3,15))
    graph.edge_list.append(edge(2,3,4))
    
    utl_obj = utility()
    
    utl_obj.kruskal(graph)   
