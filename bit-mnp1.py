#!/usr/bin/env python
'''
Sum of bit differences among all pairs
Given an integer array of n integers, find sum of bit differences in all pairs that can be 
formed from array elements. Bit difference of a pair (x, y) is count of different bits at 
same positions in binary representations of x and y. 
For example, bit difference for 2 and 7 is 2. 
Binary representation of 2 is 010 and 7 is 111 ( first and last bits differ in two numbers).
http://www.geeksforgeeks.org/count-set-bits-in-an-integer/
'''


def num_of_ones(num):
    count = 0
    while(num != 0):
        count += (num & 1)
        num = num >> 1
    return count

def solution(ll):
    sum = 0
    for i in range(0,len(ll)):
        for j in range(i+1,len(ll)):
            bit_diff = ll[i]^ll[j]
            print (str(ll[i]) + "\t" +str(ll[j])+"\t"+str(num_of_ones(bit_diff)))
            sum+=num_of_ones(bit_diff)
    return sum
    

if __name__=='__main__':
    mylist = [1,3,5]
    print ("sum of diff bits btw all unique pairs is "+str(solution(mylist)))
