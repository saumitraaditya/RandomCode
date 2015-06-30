#!/usr/bin/env python
import heapq

class edge(object):
    def __init__(self,target,weight):
        self.target= target
        self.weight = weight
        
class vertex(object):
    def __init__(self,name,min_weight = 99999):
        self.name = name
        self.adj_list = []
        self.min_weight = min_weight
        self.prev_vertex = None
        
    def __cmp__(self,other_vertex):
        return cmp(self.min_weight,other_vertex.min_weight)
        

        
class Dijkstra(object):
    def __init__(self,source_vertex,vertex_list):
        self.pq = []
        self.source = source_vertex
        for v in vertex_list:
            self.pq.append(v)
        self.source.min_weight = 0
        heapq.heapify(self.pq)
        
    def start(self):
        while (len(self.pq) != 0):
            min_vertex = heapq.heappop(self.pq)
            print ("vertex_name " + str(min_vertex.name) + ',' + "vertex_weight " + str(min_vertex.min_weight))
            
            for edge in min_vertex.adj_list:
                dst_vertex = edge.target
                weight = edge.weight
                distance_via_self = min_vertex.min_weight + weight
                if (distance_via_self < dst_vertex.min_weight):
                    dst_vertex.min_weight = distance_via_self
                    dst_vertex.prev_vertex = min_vertex
                    heapq.heapify(self.pq)
                    
if __name__ == '__main__':
    v0 = vertex(0)
    v1 = vertex(1)
    v2 = vertex(2)
    v3 = vertex(3)
    v0.adj_list.append(edge(v1,2))
    v0.adj_list.append(edge(v3,5))
    v1.adj_list.append(edge(v0,2))
    v1.adj_list.append(edge(v3,6))
    v1.adj_list.append(edge(v2,7))
    v2.adj_list.append(edge(v1,7))
    v2.adj_list.append(edge(v3,1))
    v3.adj_list.append(edge(v0,5))
    v3.adj_list.append(edge(v1,6))
    v3.adj_list.append(edge(v2,1))
    vertices = [v0,v1,v2,v3]
    
    dijkstra = Dijkstra(v0,vertices)
    dijkstra.start()
