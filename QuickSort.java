import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QuickSort {
	
	public static  void swap(int[]A,long l,long j)
	{
		int temp = A[(int) l];
		A[(int) l] = A[(int) j];
		A[(int) j] = temp;
	}
	
	public static long choosePivot(int A[], long start,long end)
	{
		//select middle index
		long middle;
		int size =(int) (end - start +1);
		if (size%2 == 0)
			middle = start + (size/2)-1;
		else
			middle = start + size/2;
		int[] candidates = {A[(int) middle],A[(int) start],A[(int) end]};
		Arrays.sort(candidates);
		if (A[(int)middle] == candidates[1])
			return middle;
		else if (A[(int)start] == candidates[1])
			return start;
		else 
			return end;
	}
	
	public static long partition(int[]A,long start,long end)
	{
		long pivot_pos = choosePivot(A,start,end);
		swap(A,start,pivot_pos);
		long i = start, j = start+1;
		int pivot = A[(int) start];
		while (j <= end)
		{
			if (A[(int) j] <= pivot)
			{
				swap(A,i+1,j);
				i++;
			}
			j++;
		}
		swap(A,start,i); // puts the pivot at its current position.
		return i;
	}
	
	public static long sort(int[]A,long start,long end)
	{
		if (start >= end)
			return 0;
		else
		{
			int pivot_position = (int) partition(A,start,end);
			long comparisonsL = sort(A,start,pivot_position-1);
			long comparisonsR = sort(A,pivot_position+1,end);
			return (end - start)+ comparisonsL + comparisonsR;
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		if (args.length < 1)
		{
			System.out.println("please enter the path of the file containing the inputs");
			
		}
		else
		{
			String filename = args[0];
			int[] InputArray = new int[10000];
			try (BufferedReader br = new BufferedReader(new FileReader(filename)))
			{
				int counter = 0;
				String line;
				while ((line = br.readLine())!= null)
				{
					InputArray[counter++] = Integer.parseInt(line);
				}
			}
//			int []A = {0,  1,  2,  3,  4,  5,  6,  7,  8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18};
//			System.out.println(choosePivot(A,4,13));
			int[] A = InputArray;
			System.out.println(A.length);
			System.out.println(A[A.length-1]);
			long comparisons = sort(A,0,A.length-1);
			/*StringBuilder S = new StringBuilder();
			for (int i = 0;i<A.length;i++)
				S.append(A[i]+" ");
			System.out.println(S);*/
			for (int i = 0;i<A.length;i++)
				System.out.println(A[i]);
			System.out.println(comparisons);
		}
	}


}

