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


thread_container = []
event = threading.Event()

def signal_term_handler(signal, frame):
    print ("interrupt signal received")
    event.set()
    for each in thread_container:
        each.join()
    print ("exiting main thread")
    sys.exit(0)
        
        
def worker(event):
    counter = 0
    while True:
        if  event.isSet():
            print ("Thread %s captured termination signal, counter is %s"%(threading.currentThread().getName(),str(counter)))
            break
        else:
            time.sleep(.1)
            counter = counter+1
            print ("name %s counter %s"%(threading.currentThread().getName(),str(counter)))
            
    


if __name__ == '__main__':
    for i in range (1,5):
        tmp_thread = threading.Thread(name = str(i),target = worker,args = (event,))
        thread_container.append(tmp_thread)
        tmp_thread.start()
        
    signal.signal(signal.SIGTERM, signal_term_handler)
    while True:
        time.sleep(.5)
        continue
    
        
    
