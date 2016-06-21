import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
public class LIS {
	
	private ArrayList<Integer> A;
	private ArrayList<Integer> cache;
	private Stack<Integer> stack;
	public LIS(ArrayList<Integer> seq)
	{
		A = seq;
		cache = new ArrayList<Integer>();
		for (int i = 0;i<A.size();i++)
			cache.add(i, 0);
	}
	// returns len of longest sequence ending at the index
	// the LIS may or may not include element at the index.
	private int lenLIS(int index)
	{
		if (cache.get(index)== 0) 
		{
			if (index == 0)
				{
					cache.set(index, 1);
					return 1;
				}
			else
			{
				int max = 0;
				int len = 0;
				for (int i = index-1;i >= 0;i--)
				{
					len = lenLIS(i);
					if (A.get(index) > A.get(i))
						len+=1;
					if (len > max)
						max = len;
				}
				cache.set(index, max);
				return max;
			}
		}
		else
			return cache.get(index);
	}
	
	private void lis(int index)
	{
		if (cache.get(index) == 1)
			stack.push(A.get(index));
		else
		{
			int max = 0;
			int len = 0;
			int index_to_prev = 0;
			for (int i = index-1;i >= 0;i--)
			{
				len = cache.get(i);
				if (A.get(index) > A.get(i))
					{
						len+=1;
					}
				if (len > max)
				{
					max = len;
					index_to_prev = i;
				}
				else if (len == max && A.get(index) > A.get(i))
				{
					max = len;
					index_to_prev = i;
				}
			}
			if (A.get(index) > A.get(index_to_prev))
				stack.push(A.get(index));
			lis(index_to_prev);
			
		}	
	}
	
	public void getLIS()
	{
		stack = new Stack<Integer>();
		lis(A.size()-1);
		StringBuilder s = new StringBuilder();
		while (!stack.isEmpty())
			s.append(stack.pop().toString()+"\t");
		System.out.println(s);
	}
	
	public int getLenLis()
	{
		return lenLIS(A.size()-1);
	}
	
	public static void main(String[] args)
	{
		ArrayList<Integer> I = new ArrayList<Integer>(Arrays.asList(2, 5, 3, 7, 11, 8, 10, 13, 6));
		LIS lis = new LIS(I);
		String result = String.format("Length of longest increasing subsequence is %d",lis.getLenLis());
		System.out.println(result);
		lis.getLIS();
	}

}
