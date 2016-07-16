import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CountInv {
	
	public static long countSplitInversions(int[]A,int left,int mid,int right)
	{
		long splitInversions = 0;
		int[] C = new int[right - left +1];
		int L= left,R= mid+1,Cindex=0;
		while (L<=mid && R<=right)
		{
			if (A[L] <= A[R])
			{
				C[Cindex++] = A[L++];
			}
			else
			{
				splitInversions += mid - L +1;
				C[Cindex++] = A[R++];
			}
		}
		while (L <= mid)
		{
			C[Cindex++] = A[L++];
		}
		while (R <= right)
		{
			C[Cindex++] = A[R++];
		}
		// copy C back to A
		for (int i = 0;i < C.length;i++)
		{
			A[left+i] = C[i];
		}
		return splitInversions;
	}
	
	public static long countInversions(int[] Input,int left,int right)
	{
		if (left >= right)
		{
			return 0;
		}
		else
		{
			int mid = (left+right)/2;
			long leftInversions = countInversions(Input,left,mid);
			long rightInversions = countInversions(Input,mid+1,right);
			long splitInversions = countSplitInversions(Input,left,mid,right);
			return leftInversions+rightInversions+splitInversions;
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
			int[] InputArray = new int[100000];
			try (BufferedReader br = new BufferedReader(new FileReader(filename)))
			{
				int counter = 0;
				String line;
				while ((line = br.readLine())!= null)
				{
					InputArray[counter++] = Integer.parseInt(line);
				}
			}
			long inversions = countInversions(InputArray,0,InputArray.length-1);
			for (int i = 0;i<InputArray.length;i++)
			{
				System.out.println(InputArray[i]);
			}
			System.out.println("Inversions --"+inversions);
		}
		
	}

}
