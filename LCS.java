
public class LCS {
	
	private String string1, string2;
	
	public LCS(String A, String B)
	{
		string1 = A;
		string2 = B;
	}
	
	public int getLenLCS()
	{
		return longestCommonSubsequence(string1,string2);
	}
	
	private int longestCommonSubsequence(String A, String B)
	{
		int lenA = A.length();
		int lenB = B.length();
		if (lenA == 0 || lenB == 0)
			return 0;
		else if (A.charAt(lenA-1) == B.charAt(lenB-1))
		{
			return 1 + longestCommonSubsequence(A.substring(0,lenA-1),B.substring(0,lenB-1));
		}
		else
			return Math.max(longestCommonSubsequence(A.substring(0,lenA-1),B.substring(0,lenB))
				, longestCommonSubsequence(A.substring(0,lenA),B.substring(0,lenB-1)));
	}
	
	public static void main(String []args)
	{
		String A = "AGGTAB";
		String B = "GXTXAYB";
		LCS X = new LCS(A,B);
		String result = String.format("Length of longest commmon subsequence is %d",X.getLenLCS());
		System.out.println(result);
	}

}


