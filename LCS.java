import java.util.*;
public class LCS {
	
	private String string1, string2;
	int [][] cache;
	Stack<Character> stack = new Stack<Character>();
	
	public LCS(String A, String B)
	{
		string1 = A;
		string2 = B;
		cache = new int[A.length()+1][B.length()+1];
		for (int i = 0;i<A.length()+1;i++)
		{
			for (int j = 0;j<B.length()+1;j++)
				cache[i][j] = -1;
		}
	}
	
	public int getLenLCS()
	{
		return lenLCS(string1,string2);
	}
	
	public String getLCS()
	{
		StringBuilder lcs= new StringBuilder();
		LoCS(string1,string2);
		while (!stack.isEmpty())
			lcs.append(stack.pop());
		return lcs.toString();
	}
	
	private int lenLCS(String A, String B)
	{
		int lenA = A.length();
		int lenB = B.length();
		if(cache[lenA][lenB]== -1) 
		{
				if (lenA == 0 || lenB == 0)
				{	
					cache[lenA][lenB] = 0;
				}
			else if (A.charAt(lenA-1) == B.charAt(lenB-1))
			{
				cache[lenA][lenB] = 1 + lenLCS(A.substring(0,lenA-1),B.substring(0,lenB-1));
			}
			else
			{
				cache[lenA][lenB] = Math.max(lenLCS(A.substring(0,lenA-1),B.substring(0,lenB))
						, lenLCS(A.substring(0,lenA),B.substring(0,lenB-1)));
			}
				return cache[lenA][lenB];
		}
		else
			return cache[lenA][lenB];
	}
	
	private void LoCS(String A, String B)
	{
		int lenA = A.length();
		int lenB = B.length();
		if (lenA == 0 || lenB == 0)
			return;
		else if (A.charAt(A.length()-1)==B.charAt(B.length()-1))
				{
					stack.push(A.charAt(A.length()-1));
					LoCS(A.substring(0,lenA-1),B.substring(0,lenB-1));
				}
		else if (cache[lenA][lenB-1]>=cache[lenA-1][lenB])
			LoCS(A.substring(0,lenA),B.substring(0,lenB-1));
		else
			LoCS(A.substring(0,lenA-1),B.substring(0,lenB));
		
	}
	
	public static void main(String []args)
	{
		String A = "ABCDGH";
		String B = "AEDFHR";
		LCS X = new LCS(A,B);
		String result = String.format("Length of longest commmon subsequence is %d",X.getLenLCS());
		System.out.println(result);
		System.out.println("LCS is :"+X.getLCS());
	}

}


