package com.cluit.util.dataTypes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.methods.ClusteringUtils;
import com.cluit.util.structures.KeyPriorityQueue_Max;
import com.cluit.util.structures.Pair;

/**A cluster is a collection of entries.
 * 
 * The class has a lot of utility functions related to clusters such as calculating centoid, finding the entry furthest 
 * from the centoid and so on.
 * 
 * @author Simon
 *
 */
public class Cluster {	
	//*******************************************************************************************************
	//region								VARIABLES		
	//*******************************************************************************************************
	private Entry centoid;
	public Pair<Double, Entry> cache = new Pair<Double, Entry>( 0.0, new Entry() );
	
	private final int dimensions;
	private final Set<Entry> members   = new HashSet<>();
	private final KeyPriorityQueue_Max<Entry> distanceQueue = new KeyPriorityQueue_Max<Entry>();
	
	//endregion *********************************************************************************************
	//region								CUNSTRUCTOR		
	//*******************************************************************************************************
	/**
	 * 
	 * @param position
	 * @param centoidIsMember
	 */
	public Cluster(double[] position) {
		if( position.length < 1){
			API_Exeption("A cluster's position must be defined and have 1 or more dimenstions!");
		}
		this.centoid = new Entry(position);
		this.dimensions = centoid.getDimensions();
	};
	
	//endregion *********************************************************************************************
	//region								STATIC METHODS	
	//*******************************************************************************************************
	
	/**Calculates a central point (centoid) from a collection of entries. Not that all entries must have the same dimensionality.
	 * 
	 * @param entries
	 * @return A new entry, with a position that is the mean of all parameter entries (NULL if entries.lenght == 0)
	 */
	public static Entry calculateCentoid(Entry[] entries){
		if( entries.length == 0)
			return null;
		
		//Fetch dimensionality for the entries and set up the coordinate array
		int dim = entries[0].getDimensions();
		double[] centoidCoordinates = new double[dim];
		
		//Add all entries positions together (for example, add all entries x-values together in one array slot, 
		//and all y-values together in the next array slot).
		for( Entry p : entries ){
			for( int i = 0; i < p.getDimensions(); i++ )
				centoidCoordinates[i] += p.getCoordinateAt(i);
		}
		
		//Divide each position by the number of entries (to get the mean of each dimension's position
		for( int i = 0; i < centoidCoordinates.length; i++)
			centoidCoordinates[i] /= entries.length;
		
		return new Entry(centoidCoordinates);
	}	
	
	/**Calculates the sum of squared errors for a given set of entries, given a centoid.<br>
	 * The calculation is simply: For each point, calculate the euclidian distance from that point to the centoid, and square the distance
	 * 
	 * @param centoid The mean position of the entries (see @link {@link Cluster.calculateCentoid} )
	 * @param entries
	 * @return
	 */
	public static double calculateSquaredError(Entry centoid, Entry[] entries){
		double out = 0;
		double dist = 0;
		for(Entry e : entries ){
			dist = ClusteringUtils.eucDistance(centoid, e);
			out += (dist*dist);
		}
		return out;
	}
	
	//endregion *********************************************************************************************
	//region								PUBLIC			
	//*******************************************************************************************************
	public int getNumberOfMembers(){
		return distanceQueue.size() == members.size() ? distanceQueue.size() : -1;
	}
	
	/**Returns the distance to the centoid for the point which is farthest from the centoid
	 * 
	 * @return The distance, if there are any members of the cluster. -1 otherwise
	 */
	public double getFurthestMembersDistance(){
		if( distanceQueue.size() == 0 )
			return -1;
		return distanceQueue.peekKey();
	}
	
	/** Calculates a new centoid for the cluster. This method also update each points distance to the centoid
	 * <br><br>
	 * Complexity = <b>O(n * d)</b>, 
	 * where <b>n</b> is the number of elements in the cluster 
	 * where <b>d</b> the number of dimensions for each point
	 */
	public void calculateCentoid(){
		int dim = centoid.getDimensions();
		double[] newCentoidCoordinates = new double[dim];
		
		for( Entry p : distanceQueue.values() ){
			for( int i = 0; i < p.getDimensions(); i++ )
				newCentoidCoordinates[i] += p.getCoordinateAt(i);
		}
		
		for( int i = 0; i < newCentoidCoordinates.length; i++)
			newCentoidCoordinates[i] /= distanceQueue.size();
		
		centoid = new Entry(newCentoidCoordinates );	
		
		updateMemberDistances();
	}
	
	/**Fetches a <b>copy</b> of the centoid of the cluster
	 * 
	 * @return A new Entry, which is a copy of the cluster's centoid
	 */
	public Entry getCentoid(){
		return new Entry(centoid);
	}
	
	/**Adds an entry to the cluster. The same entry cannot be added twice to the same cluster. 
	 * This does not automatically update the cluster centoid. To do that, call "UpdateCentoid"
	 * 
	 * @param e 
	 * @return True if the entry was added, false if it was not 
	 */
	public boolean add(Entry e){
		if( e.getDimensions() != dimensions ){
			API_Exeption("An entry cannot be added to a cluster if their dimenstions does not match! Cluster.dim = "+dimensions+" Entry.dim = "+e.getDimensions() );
			return false;
		}
		if( members.contains(e) ){
			API_Exeption("An entry cannot be added to a cluster twice! The entry "+e+" is already present in the cluster" );
			return false;
		}
		double dist;
		if( e == cache.right )
			dist = cache.left;
		else
			dist = ClusteringUtils.eucDistance(e, centoid);
		boolean a = distanceQueue.put(dist, e);
		boolean b = members.add(e);
		return a & b;
	}
	
	/**Removes a point from the cluster
	 * 
	 * @param e The point to be removed
	 * @return True if it was found. False if the point wasn't found.
	 */
	public boolean removeEntry(Entry e){
		boolean a = distanceQueue.remove(e);
		boolean b = members.remove(e);
		return a & b;		
	}
	
	/**Calculates a points distance to the clusters centoid.
	 * The result is cached (the cache stores only 1 element), to prevent
	 * the result from having to be re-computed in the near future.
	 * <br>It is therefore recommended that whenever a point checks its distance to
	 * all clusters, it should be added to a cluster before another point checks
	 * it's distances.
	 * 
	 * @param p The point
	 * @return Distance to the centoid
	 */
	public double distanceToCentoid(Entry p){
		double dist = ClusteringUtils.eucDistance(p, centoid);
		cache = new Pair<Double, Entry>(dist, p);
		return dist;
	}

	/**Checks whether a given point is member of this cluster or not
	 * 
	 * @param p The point
	 * @return True if the point is found within the cluster
	 */
	public boolean isMember(Entry e) {
		return members.contains(e);
	}
	
	/**Fetches an array of all entries that are present within this cluster. This array can have a lenght of 0, in case no
	 * entries are registered within this cluster
	 */
	public Entry[] getMembers() {
		return members.toArray( new Entry[0] );
	}
	
	/**Calculates the sum of squared errors for this cluster
	 * 
	 * @return
	 */
	public double getSquaredError(){
		return Cluster.calculateSquaredError(centoid, getMembers())	;	
	}
	
	public String toString(){
		String out = "[ ";
		for( Entry e : members ){
			out += e.toString() + " : ";
		}
		return members.size() > 0 ? out.substring(0, out.length() - 3) + " ]" : "[ ]";
	}

	//endregion *********************************************************************************************
	//region								PRIVATE			
	//*******************************************************************************************************
	/**Update each member's distance to the centoid
	 * 
	 */
	private void updateMemberDistances() {
		ArrayList<Entry> list = distanceQueue.values();
		distanceQueue.clear();
		
		for(Entry p : list){
			double newDistance = ClusteringUtils.eucDistance(centoid, p);
			distanceQueue.add(newDistance, p);
		}
	}
	private int API_Exeption(String s){
		MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Error in Cluster.java! " + s +" " + com.cluit.util.methods.MiscUtils.getStackPos(), new Exception() );
		return -1;
	}
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
