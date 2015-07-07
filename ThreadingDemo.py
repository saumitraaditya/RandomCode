# useful links
# http://pymotw.com/2/threading/
# http://eli.thegreenplace.net/2011/12/27/python-threads-communication-and-stopping

#!usr/bin/python
import logging
import random
import threading
import time
import signal
import sys
import Queue

thread_container = []
event = threading.Event()
task_queue = Queue.Queue()

def signal_term_handler(signal, frame):
    print ("interrupt signal received")
    event.set()
    for each in thread_container:
        each.join()
    print ("exiting main thread")
    sys.exit(0)
               
def producer(event):
    counter = 0
    while True:
        if  event.isSet():
            print ("Thread producer captured termination signal, counter is %s"%(str(counter)))
            break
            
        else:
            counter = counter+1
            time.sleep(2)
            task_queue.put("task" +  str(counter))
            print (" producer counter %s task_queue size is %s"%(counter,task_queue.qsize()))
        
def worker(event):
    counter = 0
    while True:
        if  event.isSet():
            print ("Thread %s captured termination signal, counter is %s"%(threading.currentThread().getName(),str(counter)))
            break
        else:
            time.sleep(1)
            counter = counter+1
            try:
                task = task_queue.get(True,.05)
            except Queue.Empty:
                print (" EXCEPTION Q EMPTY name %s counter %s"%(threading.currentThread().getName(),str(counter)))
                continue                
            print ("name %s counter %s task %s task_queue size %s"%(threading.currentThread().getName(),str(counter),task,task_queue.qsize()))
            
if __name__ == '__main__':
    for i in range (1,5):
        tmp_thread = threading.Thread(name = str(i),target = worker,args = (event,))
        thread_container.append(tmp_thread)
        tmp_thread.start()
    
    producer_thread = threading.Thread(target = producer,args = (event,)) 
    thread_container.append(producer_thread) 
    producer_thread.start() 
    signal.signal(signal.SIGTERM, signal_term_handler)
    while True:
        time.sleep(.5)
        continue
    
        
    
