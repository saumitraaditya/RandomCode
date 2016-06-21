
public class EditDistance {
	
	String str1,str2;
	public EditDistance(String str1, String str2)
	{
		this.str1 = str1;
		this.str2 = str2;
	}
	
	private int minimum(int a, int b, int c)
	{
		int min = Integer.MAX_VALUE;
		if (a < min)
			min = a;
		if (b < min)
			min = b;
		if (c < min)
			min = c;
		return min;
	}
	
	private int calcDistance(int len1,int len2)
	{
		if (len1 == 0 && len2 == 0)
			return 0;
		else if (len1 == 0)
			return len2;
		else if (len2 == 0)
			return len1;
		else
		{
			if (str1.charAt(len1-1)==str2.charAt(len2-1))
				// if last character is same then no operation is required.
				return calcDistance(len1-1,len2-1);
			else
			{
				// else we will have to carry out one of the below 3 operations
				// 1. insert -- last character after this processing becomes same.
				// 2. remove -- may or may not
				// 3. replace -- same as 1.
				// Have to evaluate all 3 options and choose the best one.
				return 1 + minimum(calcDistance(len1,len2-1),calcDistance(len1-1,len2),calcDistance(len1-1,len2-1));
			}
		}
	}
	
	public int minEditing()
	{
		return calcDistance(str1.length(),str2.length());
	}
	
	public static void main(String[] args)
	{
		String str1 = "saturday";
		String str2 = "sunday";
		EditDistance E = new EditDistance(str1,str2);
		String S = String.format("Minimum number of operations required to convert str1 to str2 is %d.",E.minEditing());
		System.out.println(S);
	}

}
