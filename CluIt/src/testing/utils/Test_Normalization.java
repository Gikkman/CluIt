package testing.utils;

import com.cluit.util.methods.ClusteringUtils;
import com.cluit.util.methods.MiscUtils;

public class Test_Normalization {

	public static void run(){
		System.out.println("Test - Normalization initiated");
		double[] test1   = {0.0, 0.5, 1.0};
		double[] facit1  = {0.0, 0.5, 1.0};
		double[] result1 = ClusteringUtils.normalize(test1);
		checkResults( result1, facit1);
		
		double[] test2   = {0.0, 0.1, 0.1, 1.0};
		double[] facit2  = {0.0, 0.1, 0.1, 1.0};
		double[] result2 = ClusteringUtils.normalize(test2);
		checkResults( result2, facit2);
		
		double[] test3   = {-1000, 0, 1000};
		double[] facit3  = {0.0, 0.5, 1.0};
		double[] result3 = ClusteringUtils.normalize(test3);
		checkResults( result3, facit3);
		
		double[] test4   = {0, -10, 10, 5};
		double[] facit4  = {0.5, 0.0, 1.0, 0.75};
		double[] result4 = ClusteringUtils.normalize(test4);
		checkResults( result4, facit4);
		
		double[] test5   = {10};
		double[] facit5  = {0}; 
		double[] result5 = ClusteringUtils.normalize(test5);
		checkResults(result5, facit5);
		
		double[] test6   = {10, 10, 10};
		double[] facit6  = {0, 0, 0}; 
		double[] result6 = ClusteringUtils.normalize(test6);
		checkResults(result6, facit6);
		
		double[] test7   = {0, 100, 10, 50, 75};
		double[] facit7  = {0, 1, 0.1, 0.5, 0.75}; 
		double[] result7 = ClusteringUtils.normalize(test7);
		checkResults(result7, facit7);
		
		double[] test8   = {0, -100, -10, -50, -75};
		double[] facit8  = {1, 0, 0.9, 0.5, 0.25}; 
		double[] result8 = ClusteringUtils.normalize(test8);
		checkResults(result8, facit8);
		
		double[] test9   = {100000, 100010, 100005};
		double[] facit9  = {0, 1, 0.5}; 
		double[] result9 = ClusteringUtils.normalize(test9);
		checkResults(result9, facit9);
		
		System.out.println("Test - Normalization finalized");
	}

	private static void checkResults(double[] result, double ... facit) {
		if( result.length != facit.length ){
			System.err.println("Error in Test_Normalization! Result.lenght != facit.lenght [ " + result.length + " != " + facit.length + "]" + MiscUtils.getStackPos());
			return;
		}
		for( int i = 0; i < result.length; i++){
			if( Math.abs( result[i] - facit[i] ) > 0.00001 ){
				System.err.println("Error in Test_Normalization! Result["+i+"] != facit["+i+"] -- Result["+i+"] = "+result[i]+" : facit["+i+"] = "+facit[i]+" " + MiscUtils.getStackPos());
			}
		}
	}
}
