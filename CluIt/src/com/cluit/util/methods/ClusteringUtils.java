package com.cluit.util.methods;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.dataTypes.Entry;

/**Helper class which contains functions for performing different calculations
 * of importance for the clustering algorithms.
 * 
 * @author Simon Jonasson - 2015-07-07
 *
 */
public class ClusteringUtils {

	/**Fisher-Yates shuffles an array of Objects
	 * 
	 * @param elem The list which's elements will be randomized
	 * @param rng A random-number generator, derived from the {@link java.util.Random} class
	 */
	public static <T> void fyShuffle(T[] elem, Random rng){
		int index = 0;
		T temp = null;
		
		//Fisher–Yates shuffle the 'elem' array
		for (int i = elem.length - 1; i > 0; i--) {
			index = rng.nextInt( i + 1 );
			temp = elem[index];
			elem[index] = elem[i];
			elem[i] = temp;
		}
	}
	
	/**Fisher-Yates shuffles an array of Objects
	 * 
	 * @param elem The list which's elements will be randomized
	 */
	public static <T> void fyShuffle(T[] elem){
		fyShuffle(elem, new Random() );
	}
	
	/**Creates a list of n unique integers, in the range from min (inclusive) to max (exclusive)
	 * 
	 * @param min The minimum number allowed (exclusive)
	 * @param max The maximum number allowed (exclusive)
	 * @param n The amount of unique numbers generated. Must uphold: n <= (max - min)
	 * @param rng A random-number generator, derived from the {@link java.util.Random} class
	 * @return An int[] with the n unique numbers
	 * @throws Exception 
	 */
	public static int[] distinctRandom(int min, int max, int n, Random rng) {
		if( n > max - min)
			System.err.println("Error. The requested number of random numbers are higher than allowed.\nSource: " + new Exception().getStackTrace() );	
		
		//Create an ordered list with numbers [min, min+1, ... max-2, max-1]
		Integer[] list = new Integer[max - min];
		for( int i = 0; i < max - min; i++){
			list[i] = i + min;
		}
		//Fisher-Yates shuffle the list
		fyShuffle(list, rng);
		//Take the n first numbers from the randomized list and place them in 'out'
		int[] out = new int[n];
		for( int i = 0; i < n; i++){
			out[i] = list[i];
		}
		
		return out;		
	}

	public static int[] distinctRandom(int min, int max, Random rng) {
		return distinctRandom(min, max, max, rng);
	}
	
	/**Creates a distance matrix of the distances between each objects element-matrix. The distance is calculated by looking at each Entry's feature-vector, and
	 * calculating the euclidian distance between the two objects hyperspace positions.
	 * 
	 * <br><br>
	 * Keep in mind, if <b>i</b> == <b>j</b>, the distance is 0
	 * <br>
	 * The output matrix will be ordered in the same fashion as the supplied list.
	 * 
	 * @param elements An array of Entrys, where each cell in an entry corresponds to a Feature 
	 * @return The distance matrix. The double at <b>out[i][j]</b> indicates the distance between object <b>i</b> and <b>j</b>
	 */
	public static double[][] calcDistanceMatrix( List<Entry> elements ){
		double[][] out = new double[elements.size()][elements.size()];
		double temp = 0;
		
		//To be a bit more effective, move down the column and through the row of the distance matrix at the same time
		for( int i = 0; i < elements.size(); i++){
			for( int j = i; j < elements.size(); j++){
				if( i == j){
					out[i][j] = 0;
				} else {
					temp = eucDistance(elements.get(i), elements.get(j));
					out[i][j] = temp;
					out[j][i] = temp;
				}
			}
		}
		
		return out;
	}
	
	/**Reformats the array and sends it to {@linkplain com.cluit.util.methods.ClusteringUtils#calcDistanceMatrix(List) }
	 * 
	 * @param elements  An array of Entrys, where each cell in an entry corresponds to a Feature 
	 * @return The distance matrix. The double at <b>out[i][j]</b> indicates the distance between object <b>i</b> and <b>j</b>
	 */
	public static double[][] calcDistanceMatrix( Entry[] elements ){
		return calcDistanceMatrix( Arrays.asList(elements) );
	}	
	
	/**Calculates the euclidian distance between two points, in n-dimensional space.
	 * 
	 * Note that vectorA and vectorB must be of the same dimension. Their relative order does not matter (ie, dist(A,B) == dist(B,A) )
	 * 
	 * @param pointA 
	 * @param pointB
	 * @return
	 */
	public static double eucDistance(Entry pointA, Entry pointB){
		if( pointA.getDimensions() != pointB.getDimensions()){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL ,"Error in ClusteringUtils.java! The lenght of vectorA ("+pointA.getDimensions()+") and vectorB ("+pointB.getDimensions() + ") does not match! " + com.cluit.util.methods.MiscUtils.getStackPos(), new Exception() );
			return -1;
		}
		/* Euclidian distance = sqrt( x^2 + y^2 + z^2 + ... + m^2 + n^2 ), for n-dimensional space
		 * 
		 * Since we are working with two objects here, we need to take (A.x - B.x)^2, for each dimension
		 */
		double out = 0;
		for( int i = 0; i < pointA.getDimensions(); i++){
			double temp = pointA.getCoordinateAt(i) - pointB.getCoordinateAt(i);
			temp *= temp;
			out += temp;
		}		
		out = Math.sqrt(out);
				
		return out;
	}
	
	/**Calculates the sum of squared error distances within a cluster.<br>
	 * The calculation is performed the following way: For each member, calculate euclidian distance from the member to the centoid and square it. Then, add all these squared distances together.
	 * 
	 * @param centoid The clusters centoid
	 * @param members The clusters member entries
	 * @return
	 */
	public static double sumOfSquareErrors(Entry centoid, Entry[] members){
		double out = 0;
		double eucDist = 0;
		
		for(Entry e : members){
			eucDist = eucDistance(centoid, e);
			out += (eucDist*eucDist);
		}
		
		return out;
	}
	
}
