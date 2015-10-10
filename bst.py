#!/usr/bin/env python


class node:
    def __init__(self,val,lc = None,rc = None):
        self.v = val
        self.lc = lc
        self.rc = rc
        
class tree:
    def __init__(self, root = None):
        self.root = root
        
    def insert(self,val):
        temp = node(val)
        if (self.root == None):
            self.root = temp
        else:
            self.insert_node(temp,self.root)
            
    def insert_node(self,node,parent):
        if (node.v <= parent.v):
            if (parent.lc != None):
                self.insert_node(node,parent.lc)
            else:
                parent.lc = node
                return 
        else:
            if (parent.rc != None):
                self.insert_node(node,parent.rc)
            else:
                parent.rc = node
                return
                
    def inorder(self,node):
        if (node.rc == None and node.lc == None):
            print node.v
            return
        else:
            if (node.lc != None):
                self.inorder(node.lc)
            print(node.v)
            if (node.rc != None):
                self.inorder(node.rc)
                
    def display(self):
        self.inorder(self.root)
        
    def lookup(self,val):
        if self.root.v == val:
            return (None,self.root)
        else:
            return self.looknode(self.root,val)
            
    def looknode(self,parent,val):
        if (parent.rc != None and parent.rc.v == val):
            return (parent,parent.rc)
        elif (parent.lc != None and parent.lc.v == val):
            return (parent,parent.lc)
        elif (parent.lc != None and val < parent.v):
            return self.looknode(parent.lc,val)
        elif (parent.rc !=None and val > parent):
            return self.looknode(parent.rc,val)
        else:
            return None
            
    def delete(self,val):
        self.root = self.deleteNode(self.root,val)
        
    def deleteNode(self,curr,val):
        if (curr == None):
            print "Node not found"
        else:
            if (curr.v == val):
                if (curr.lc == None):
                    return curr.rc
                elif (curr.rc == None):
                    return curr.lc
                else:
                    # get rightmost value in left subtree
                    rightmost_val = self.rightmost(curr.lc)
                    curr.v = rightmost_val
                    curr.lc = self.deleteNode(curr.lc,rightmost_val)
            elif (val < curr.v):
                curr.lc = self.deleteNode(curr.lc,val)
            elif (val > curr.v):
                curr.rc = self.deleteNode(curr.rc,val)
        return curr
        
    def rightmost(self,node):
        if (node.rc == None):
            return node.v
        else:
            return self.rightmost(node.lc)
            
            
'''public void delete(T toDelete)
   {
      root = delete(root, toDelete);
   }
   private Node<T> delete(Node<T> p, T toDelete)
   {
      if (p == null)  throw new RuntimeException("cannot delete.");
      else
      if (compare(toDelete, p.data) < 0)
      p.left = delete (p.left, toDelete);
      else
      if (compare(toDelete, p.data)  > 0)
      p.right = delete (p.right, toDelete);
      else
      {
         if (p.left == null) return p.right;
         else
         if (p.right == null) return p.left;
         else
         {
         // get data from the rightmost node in the left subtree
            p.data = retrieveData(p.left);
         // delete the rightmost node in the left subtree
            p.left =  delete(p.left, p.data) ;
         }
      }
      return p;
   }
   private T retrieveData(Node<T> p)
   {
      while (p.right != null) p = p.right;

      return p.data;
   }
 '''
        

    
                


if __name__=="__main__":
    t = tree()
    t.insert(20)
    t.insert(5)
    t.insert(67)
    t.insert(2)
    t.insert(1)
    t.insert(150)
    
    t.display()
    
    '''print (len(t.lookup(67)))
    parent, curr = t.lookup(67)
    
    print parent.v
    print curr.v'''
    
    t.delete(20)
    
    t.display()
