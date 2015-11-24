package testing.clustering;

import com.cluit.util.dataTypes.Entry;
import com.cluit.util.methods.ClusteringUtils;
import com.cluit.util.methods.MiscUtils;

import testing._helpers.FakeRandom;
import testing._helpers.tsHelp;



public class Test_Helper {
	public static void run(String[] args){
		testDistinctRandom();
		testEucDistance();
		testCalcDistanceMatrix();
		testNormalize();
	}

	/** Tests the DistinctRandom function
	 * 
	 */
	public static void testDistinctRandom() {
		System.out.println("Test - DistinctRandom initialized");
		
		int[] array, solution;		
		FakeRandom r;
		
		r = new FakeRandom(0, 1, 2, 3, 4);
		array = ClusteringUtils.distinctRandom(0, 10, 5, r);
		solution = new int[]{5, 6, 7, 8, 9};
		compareArray(array, solution);
		
		array = ClusteringUtils.distinctRandom(0, 10, 10, r);
		for( int i = 0; i < 10; i++){
			boolean t = false;
			for(int j = 0; j < 10; j++){
				if( array[j] == i)
					t = true;
			}
			if( !t )
				System.out.println("Error! Array " + tsHelp.atos(array) + " did not contain " + i );
		}
		
		System.out.println("Test - DistinctRandom finalized");
	}
	
	public static void compareArray(int[] a, int[] b){
		for( int i = 0; i < a.length; i++){
			if( a[i] != b[i] ){
				System.err.println("Error! Array missmatch! A = "+a[i]+" B = "+b[i]);
			}
		}
	}

	/** Tests the CalcDistanceMatrix function
	 * 
	 */
	public static void testCalcDistanceMatrix() {
		System.out.println("Test - CalcDistanceMatrix initialized");
		double[][] facit   = { {0, 1, 2, 1, Math.sqrt(2), Math.sqrt(5), 2,  Math.sqrt(5), Math.sqrt(8) },
							   {1, 0, 1, Math.sqrt(2), 1, Math.sqrt(2), Math.sqrt(5), 2,  Math.sqrt(5) },
							   {2, 1, 0, Math.sqrt(5), Math.sqrt(2), 1, Math.sqrt(8), Math.sqrt(5), 2  },
							   {1, Math.sqrt(2), Math.sqrt(5), 0, 1, 2, 1, Math.sqrt(2), Math.sqrt(5)  },
							   {Math.sqrt(2), 1, Math.sqrt(2), 1, 0, 1, Math.sqrt(2), 1, Math.sqrt(2)  },
							   {Math.sqrt(5), Math.sqrt(2), 1, 2, 1, 0, Math.sqrt(5), Math.sqrt(2), 1  },
							   {2, Math.sqrt(5), Math.sqrt(8), 1, Math.sqrt(2), Math.sqrt(5), 0, 1, 2  },
							   {Math.sqrt(5), 2, Math.sqrt(5), Math.sqrt(2), 1, Math.sqrt(2), 1, 0, 1  },
							   {Math.sqrt(8), Math.sqrt(5), 2, Math.sqrt(5), Math.sqrt(2), 1, 2, 1, 0  } };
				
				
		Entry[] entries = { new Entry(-1.0, 1.0), new Entry(0.0, 1.0), new Entry(1.0, 1.0),
							new Entry(-1.0, 0.0), new Entry(0.0, 0.0), new Entry(1.0, 0.0), 
							new Entry(-1.0,-1.0), new Entry(0.0,-1.0), new Entry(1.0,-1.0) };
		
		double[][] matrix = ClusteringUtils.calcDistanceMatrix(entries);
		
		compareIntMatrices( matrix, facit);
		
		System.out.println("Test - CalcDistanceMatrix finalized");
	}

	/** Tests the EucDistance function
	 * 
	 */
	public static void testEucDistance() {
		System.out.println("Test - EucDistance initialized");
		Entry A, B;		
		A = new Entry (2.5);
		B = new Entry (6.5);
		testEucDistCorrectness(A, B, 4);
		
		A = new Entry (3.25);
		B = new Entry (-1.75);
		testEucDistCorrectness(A, B, 5);
		
		A = new Entry (15.0, 0.0);
		B = new Entry (-1.0, 0.0);
		testEucDistCorrectness(A, B, 16);
		
		A = new Entry (3.0, 2.0);
		B = new Entry (1.0, 1.0);
		testEucDistCorrectness(A, B, 2.236068);
		
		A = new Entry (5.0, -7.0);
		B = new Entry (3.0, 2.0);
		testEucDistCorrectness(A, B, 9.219544);
		
		A = new Entry (3.0, 4.0, 5.0, 6.0);
		B = new Entry (1.0, 8.0, -6.0, -2.0);
		testEucDistCorrectness(A, B, 14.317821);
		
		A = new Entry (2.75, 6.66, 7.85, 0.015);
		B = new Entry (12.6, 0.085, -6.5, -2.01);
		testEucDistCorrectness(A, B, 18.715669);
				
		System.out.println("Test - EucDistance finalized");
	}
	
	public static void testEucDistCorrectness(Entry A, Entry B, double soultion){
		//Use the clHelp function
		double calc = ClusteringUtils.eucDistance(A, B);
		
		if( Math.abs(calc-soultion) > 0.001 ){
			System.err.println("Error! Calculation missmatch! A = "+tsHelp.atos(A)+" B = "+tsHelp.atos(B)
					+"\neucDistance = "+calc+"\nMath = "+soultion);
		}
	}
	
	/**
	 * Debug function for displaying a matrix
	 * 
	 * @param matrix The matrix to display
	 */
	public static void printMatrix(double[][] matrix) {
		int dim = matrix.length;
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				System.out.printf("%5f", matrix[i][j]);
				if (i == j && matrix[i][j] != 0){
				//	System.err.println("Error at i = " + i + " j = " + j +" Value = " + matrix[i][j]);
				}
			}
			System.out.println();
		}

		System.out.println();
	}
	
	/** Compares two matrices and prints errors if they are not exactly similar
	 * 
	 * @param file	Name of the loaded file
	 * @param myMatrix The generated matrix
	 * @param facit The known correct matrix
	 */
	static void compareIntMatrices(double[][] matrix, double[][] facit){
		if( matrix.length != facit.length )
			System.err.println("1st degree lenght error for distance matrix");
		for( int i = 0; i < facit.length; i++)
			if( matrix[i].length != facit[i].length )
				System.err.println("2nd degree lenght error for distance matrix" + ". At " + i + ". myMatrix.len " + matrix.length +" facit.len " + facit.length);
		
		for( int i = 0; i < facit.length; i++)
			for(int j = 0; j < facit[i].length; j++)
				if( Math.abs( matrix[i][j] - facit[i][j]) > 0.00001 ){
					System.err.println("Value error at [" + i + ", " + j + "] for distance matrix" +"\nValue at facit: " + facit[i][j] + " Value at myMatrix: " + matrix[i][j]);
				}
	}
	
	static void testNormalize() {
		System.out.println("Test - Normalize initialized");
		double[] test1 = {1, 2, 3, 4, 5};
		double[] facit1= {0.0, 0.25, 0.5, 0.75, 1.0};
		
		double[] normalized1 = ClusteringUtils.normalize(test1);
		checkNormalize( normalized1, facit1);
		////////////////////////////////////////////////////////////////////////////////////////////////
		double[] test2 = {-10, -5, 0, 5, 10};
		double[] facit2= {0.0, 0.25, 0.5, 0.75, 1.0};
		
		double[] normalized2 = ClusteringUtils.normalize(test2);
		checkNormalize( normalized2, facit2);
		/////////////////////////////////////////////////////////////////////////////////////////////////
		double[] test3 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		double[] facit3= {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};
		
		double[] normalized3 = ClusteringUtils.normalize(test3);
		checkNormalize( normalized3, facit3);
		////////////////////////////////////////////////////////////////////////////////////////////////
		double[] test4 = {-20, -10, -5, 0, 10};
		double[] facit4= {0.0, (double) 1/3, 0.5, (double) 2/3, 1.0};
		
		double[] normalized4 = ClusteringUtils.normalize(test4, -20, 10);
		checkNormalize( normalized4, facit4);
		
		System.out.println("Test - Normalize finalized");
	}

	private static void checkNormalize(double[] normalized, double[] facit) {
		if( !tsHelp.ArraysMatch(normalized, facit) )
			System.err.println("Error in Test - Normalize. Arrays miss match! " + MiscUtils.getStackPos());
	}
}
