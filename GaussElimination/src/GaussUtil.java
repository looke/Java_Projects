public class GaussUtil
{
	public static void swapRows(float[][] matrix, int row_from, int row_to)
	{
		if (row_from >= matrix.length || row_to >= matrix.length)
		{
			System.out.print("Illegal input row number. Matrix row number:");
			System.out.print(matrix.length);
			return;
		}

		float[] temp_from = new float[matrix[row_from].length];
		for (int j = 0; j < matrix[row_from].length; j++)
		{
			temp_from[j] = matrix[row_from][j];
		}

		if (matrix[row_from].length != matrix[row_to].length)
		{
			System.out.print("Matrix row has different length.");
			return;
		}

		for (int j = 0; j < matrix[row_to].length; j++)
		{
			matrix[row_from][j] = matrix[row_to][j];
			matrix[row_to][j] = temp_from[j];
		}

	}

	public static int findPivotRow(float[][] matrix, int start_row)
	{
		int pivotRow = start_row;
		float maxElement = matrix[start_row][start_row];
		
		for (int i = start_row; i < matrix.length; i++)
		{
			if(Math.abs(matrix[i][start_row]) > Math.abs(maxElement))
			{
				pivotRow = i;
				maxElement = matrix[i][start_row];
			}
		}
		return pivotRow;
	}
	
	public static void eliminateRow(float[][] matrix, int pivotRow)
	{
		for (int i = pivotRow+1; i < matrix.length; i++)
		{
			float currentPivotValue = matrix[i][pivotRow];
			for(int j=pivotRow; j<matrix[i].length; j++)
			{
				matrix[i][j] = matrix[i][j] - currentPivotValue*matrix[pivotRow][j];
			}
		}
		
	}
	
	public static void normalizeRow(float[][] matrix, int pivotRow)
	{
		float pivotValue = matrix[pivotRow][pivotRow];
		for(int j=pivotRow; j<matrix[pivotRow].length; j++)
		{
			matrix[pivotRow][j] = matrix[pivotRow][j]/pivotValue;
		}
	}
	
	public static void gaussElim(float[][] matrix)
	{
		int rowNum = matrix.length;
		//int columNum = matrix[0].length;
		
		for(int i=0; i<rowNum; i++)
		{
			System.out.println("LOOP:" + i);
			
			int pivotRow = findPivotRow(matrix, i);
			System.out.println("PivotRow:" + pivotRow);
			
			swapRows(matrix, i, pivotRow);
			System.out.println("After swap");
			printMatrix(matrix);
			
			normalizeRow(matrix, i);
			System.out.println("After Normalization");
			printMatrix(matrix);
			
			eliminateRow(matrix,i);
			System.out.println("After elimnate");
			printMatrix(matrix);
		}
	}
	
	
	public static void printMatrix(float[][] matrix)
	{
		for (int i = 0; i < matrix.length; i++)
		{

			for (int j = 0; j < matrix[i].length; j++)
			{
				System.out.print(matrix[i][j]);
				System.out.print(",");
			}
			System.out.print("\n");
		}
	}
	
	public static void calcResult(float[][] matrix)
	{
		int rowNum = matrix.length;
		int colNum = matrix[0].length;
		
		if(rowNum < colNum-1)
		{
			System.out.println("Do not have enough rows to solve.");
			return;
		}
		int arguRank = argumentRank(matrix);
		int coRank = coRank(matrix);
		System.out.println("Argument Rank of Matrix:" +arguRank + " . Co Rank of Matrix:"+coRank );
		if(arguRank > coRank)
		{
			System.out.println("No root exist.");
			return;
		}
		
		if(arguRank == coRank)
		{
			System.out.println("Only one root exist.");
		}
		
		
		float[] roots = new float[rowNum];
		System.out.println("There are " + String.valueOf(rowNum) + " roots.");
		
		//roots[rowNum-1] = matrix[rowNum-1][rowNum];
		
		for(int i=rowNum-1; i>=1; i--)
		{
			System.out.println("LOOP:" + String.valueOf(i));
			float temp = matrix[i][rowNum];
			
			//float matrixRowSum = 0.0f;
			for(int j=i-1; j>=0; j--)
			{
				//matrixRowSum = matrixRowSum+temp*matrix[i][j];
				matrix[j][rowNum] = matrix[j][rowNum] - matrix[j][i] * temp;
				matrix[j][i] = 0;
			}
			
			printMatrix(matrix);
			//roots[i] = matrix[i][rowNum] - matrixRowSum;
		}
	}
	
	public static int argumentRank(float[][] matrix)
	{
		int rowNum = matrix.length;
		
		int rank = 0;
		for(int i=0; i<rowNum; i++)
		{
			if(!isRowAllZeroForArguMatrix(matrix[i]))
			{
				rank++;
			}
		}
		return rank;
	}
	
	public static int coRank(float[][] matrix)
	{
		int rowNum = matrix.length;
		//int colNum = matrix[0].length;
		int rank = 0;
		for(int i=0; i<rowNum; i++)
		{
			if(!isRowAllZeroForCoMatrix(matrix[i]))
			{
				rank++;
			}
		}
		return rank;
	}
	
	private static boolean isRowAllZeroForCoMatrix(float[] coMatrixRow)
	{
		return isRowAllZero(coMatrixRow, false);
	}
	
	private static boolean isRowAllZeroForArguMatrix(float[] arguMatrixRow)
	{
		return isRowAllZero(arguMatrixRow, true);
	}
	
	private static boolean isRowAllZero(float[] matrixRow, boolean isArgu)
	{
		int endLength=0;
		if(isArgu)
		{
			endLength = matrixRow.length;
		}
		else
		{
			endLength = matrixRow.length - 1;
		}
		int zeroNum = 0;
		
		//calc zero number
		for(int i=0; i<endLength; i++)
		{
			if(matrixRow[i] == 0)
			{
				zeroNum++;
			}
		}
		if(zeroNum == endLength)
		{
			return true;
		}
		else
			return false;
	}
}
