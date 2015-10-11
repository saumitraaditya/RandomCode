#!/usr/bin/env python


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
