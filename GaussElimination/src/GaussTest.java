import java.math.*;

public class GaussTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		float[][] matrix = new float[4][5];
		
		matrix[0][0] = 2f;
		matrix[0][1] = 2f;
		matrix[0][2] = 5f;
		matrix[0][3] = 8f;
		matrix[0][4] = 8f;
		
		matrix[1][0] = 3f;
		matrix[1][1] = 4f;
		matrix[1][2] = 4f;
		matrix[1][3] = 6f;
		matrix[1][4] = 6f;
		
		matrix[2][0] = 5f;
		matrix[2][1] = 6f;
		matrix[2][2] = 6f;
		matrix[2][3] = 4f;
		matrix[2][4] = 4f;
		
		matrix[3][0] = 12f;
		matrix[3][1] = 8f;
		matrix[3][2] = 10f;
		matrix[3][3] = 4f;
		matrix[3][4] = 4f;
		
		float[][] matrix_2 = new float[4][4];
		matrix_2[0][0] = 1f;
		matrix_2[0][1] = 1f;
		matrix_2[0][2] = -3f;
		matrix_2[0][3] = -1f;
		
		
		matrix_2[1][0] = 2f;
		matrix_2[1][1] = 1f;
		matrix_2[1][2] = -2f;
		matrix_2[1][3] = 1f;
		
		
		matrix_2[2][0] = 1f;
		matrix_2[2][1] = 2f;
		matrix_2[2][2] = -3f;
		matrix_2[2][3] = 1f;
		
		
		matrix_2[3][0] = 1f;
		matrix_2[3][1] = 1f;
		matrix_2[3][2] = 1f;
		matrix_2[3][3] = 100f;
		
		
		float[][] matrix_3 = new float[4][5];
		matrix_3[0][0] = 2f;
		matrix_3[0][1] = 3f;
		matrix_3[0][2] = 11f;
		matrix_3[0][3] = 5f;
		matrix_3[0][4] = 2f;
		
		matrix_3[1][0] = 1f;
		matrix_3[1][1] = 1f;
		matrix_3[1][2] = 5f;
		matrix_3[1][3] = 2f;
		matrix_3[1][4] = 1f;
		
		matrix_3[2][0] = 2f;
		matrix_3[2][1] = 1f;
		matrix_3[2][2] = 3f;
		matrix_3[2][3] = 2f;
		matrix_3[2][4] = -3f;
		
		matrix_3[3][0] = 1f;
		matrix_3[3][1] = 1f;
		matrix_3[3][2] = 3f;
		matrix_3[3][3] = 4f;
		matrix_3[3][4] = -3f;
		GaussUtil.printMatrix(matrix_3);
		
		GaussUtil.gaussElim(matrix_3);
		
		GaussUtil.calcResult(matrix_3);
		/*
		int pivot = GaussUtil.findPivotRow(matrix, 0);
		for (int j = 0; j < matrix[pivot].length; j++)
		{
			System.out.print(matrix[pivot][j]);
			System.out.print(",");
		}
		System.out.print("\n");
		
		GaussUtil.swapRows(matrix,1,2);
		
		GaussUtil.printMatrix(matrix);
		pivot = GaussUtil.findPivotRow(matrix, 0);
		for (int j = 0; j < matrix[pivot].length; j++)
		{
			System.out.print(matrix[pivot][j]);
			System.out.print(",");
		}
		System.out.print("\n");
		*/
	}
	
	

}
