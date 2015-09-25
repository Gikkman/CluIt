package com.cluit.util.dataTypes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**A PointSpace is a construction which holds a number of data entry. These entry are intended to be mapped to clusters.
 * The PointSpace handles all the methods related to these entry and clusters, such as keeping track of each point's and cluster's ID
 * 
 * @author Simon
 *
 */


public class Space {
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private final BiMap<Entry, Integer> 	entry_to_id   		 = HashBiMap.create();
	private final BiMap<Cluster, Integer> 	cluster_to_id 		 = HashBiMap.create();
	
	private final Map<Entry, Cluster>	   	entry_to_cluster 	 = new HashMap<>();
	
	private final Set<Entry>				free_entrys			 = new HashSet<>();
	
	private final int 						dimensions;
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private double[][] 						distances;
	private int 							nextEntryID = 0, nextClusterID = 0;
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/**A space must have at least one entry in it, and all entries and subsequently added clusters must match the space's dimensions.
	 * 
	 * @param dimensions
	 * @param entries
	 * @return A space, with the argument entries and with the designated dimensions (NULL if the arguments didn't live up to the contract)
	 */
	public static Space create(int dimensions, Entry ... entries){
		Space out = new Space(dimensions);
		if(dimensions <= 0) {
			out.exception("A space's dimensions must be a positive integer. Recieved "+dimensions);
			return null;
		}
		if( (entries == null || entries.length < 1) ){
			out.exception( "A space must contain at least one entry. Recieved "+ Arrays.toString(entries) );
			return null;
		}
		for(Entry e : entries) {
			if( e.getDimensions() != dimensions ){
				out.exception("Entry "+e+" is defined with the wrong number of dimensions. Space.dimensions = "+dimensions);
				continue;
			}
			out.addEntry(e);	
		}
		
		out.distances = com.cluit.util.methods.ClusteringUtils.calcDistanceMatrix( entries );		
		return out;
	}
	
	public static Space create(int dimensions, List<Entry> entries ){
		return Space.create( dimensions, (Entry[]) entries.toArray() );
	}
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/	
	
	/**Creates a new Cluster with a centoid described by [coords]
	 * 
	 * @return The name of the new cluster <b>OR</b> -1 if the cluster couldn't be created
	 */
	public int addCluster(double ... coords ){
		if(  coords.length != dimensions)
			return exception(  "Attemp to create cluster at "  +Arrays.toString(coords)+ " failed. Dimenssion missmatch, Space is "+dimensions+"-dimensional");
		
		Cluster cluster = new Cluster(coords);	
		cluster_to_id.put(cluster, nextClusterID++);			
		return cluster_to_id.get(cluster);
	}
	
	/**Adds an Entry e to the designated Cluster c
	 * 
	 * @return -1 if an error occurred, otherwise c
	 */
	public int addEntryToCluster(Entry entry, int cluster){
		if(  cluster >= nextClusterID)
			return exception(  "The requested cluster "+cluster+" does not exist." );
		if( !entry_to_id.containsKey(entry))
			return exception(  "The entry "+entry+" does not exist in the current space.");
		
		Cluster clu = cluster_to_id.inverse().get(cluster);
		return addEntryToCluster(entry, clu);
	}

	/**Checks which cluster the argument entry is member of.
	 * 
	 * @param e
	 * @return The name of the cluster <b>OR</b> -1 (if it isn't member in any cluster)
	 */
	public int getEntryMembership(Entry e){
		return cluster_to_id.getOrDefault( entry_to_cluster.get(e), -1 );
	}
	
	/**Calculates the distance between to different entries
	 * 
	 * @param e1
	 * @param e2
	 * @return The euclidian distance between the entries
	 */
	public double getDistance(Entry e1, Entry e2){
		if( !entry_to_id.containsKey(e1) )
			return exception( "The entry "+e1+" does not exist in the current space." ); 
		if( !entry_to_id.containsKey(e2) )
			return exception( "The entry "+e2+" does not exist in the current space." );
		
		int entry1 = entry_to_id.get(e1);
		int entry2 = entry_to_id.get(e2);
		return distances[entry1][entry2];
	}
	
	/**Calculates the distance between two clusters
	 * 
	 * @param cluster1 ID for cluster 1
	 * @param cluster2 ID for cluster 2
	 * @return The euclidian distance between the clusters
	 */
	public double getDistance(int cluster1, int cluster2){
		if( cluster1 >= nextClusterID )
			return exception( "The requested cluster "+cluster1+" does not exist. The cluster named "+cluster2+" does not exist");
		if( cluster2 >= nextClusterID)
			return exception( "The requested cluster "+cluster1+" does not exist. The cluster named "+cluster2+" does not exist");
		
		
		Cluster c1 = cluster_to_id.inverse().get(cluster1);
		Cluster c2 = cluster_to_id.inverse().get(cluster2);
		
		return com.cluit.util.methods.ClusteringUtils.eucDistance( c1.getCentoid() , c2.getCentoid() );
	}
	
	public void updateCentoids() {
		for( Cluster c : cluster_to_id.keySet() )
			c.calculateCentoid();
	}
	
	public final Entry clusterCentoid(int cluster) {
		return cluster_to_id.inverse().get(cluster).getCentoid();
	}
	
	public final Entry[] getFreeEntries(){ return free_entrys.toArray( new Entry[0] ); }
	public final Entry[] getAllEntries(){  return entry_to_id.keySet().toArray( new Entry[0] ); }
	public final Entry[] getClusteredEntries(){ return entry_to_cluster.keySet().toArray( new Entry[0] ); }
	
	public final Entry[] getEntriesInCluster(int cluster){ return cluster_to_id.inverse().get(cluster).getMembers(); }
	public final Entry getCentoid(int cluster){ return cluster_to_id.inverse().get(cluster).getCentoid(); }
	
	public double getSquaredError(int cluster){ return cluster_to_id.inverse().get(cluster).getSquaredError(); }
	
	public int getDimensions() {return dimensions; }
	public int getNumberOfEntries()  { return nextEntryID; };
	public int getNumberOfClusters() { return nextClusterID; };
	public boolean isClustered(Entry e) { return !free_entrys.contains(e); }
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	private Space(int dimensions) { this.dimensions = dimensions; };	
	
	//Adds an entry to the space
	private boolean addEntry(Entry e){
		entry_to_id.put(e, nextEntryID++);
		if( !free_entrys.add(e) ) {
			exception("Could not add "+e+" to free entries! The entry is most likely a douplicate. Each entry in a space must be unique");
			return false;
		}
		return true;
	}
	
	//If Entry is unclustered, add it to c and remove it from free entries.
	//If Entry is already clustered, move it from old cluster to c
	private int addEntryToCluster(Entry e, Cluster c){
		if( free_entrys.contains(e) ){
			entry_to_cluster.put(e, c);
			if(!c.add(e) ) 				exception("Could not add entry "+e+" to cluster "+c);
			if(!free_entrys.remove(e) ) exception("Could not remove "+e+" from free_entries");
		}
		else {
			if( !entry_to_cluster.get(e).removeEntry(e) ) exception("Could not remove entry "+e+" from cluster "+c);
			entry_to_cluster.replace(e, c);	
		}
		return cluster_to_id.get( entry_to_cluster.get(e) );
	}
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/
	/*****************************************************************************************************************/

	private int exception( String message){
		MethodMapper.invoke(Const.METHOD_EXCEPTION_API, "Error in Space.java! " + message +" " + com.cluit.util.methods.MiscUtils.getStackPos(), new Exception() );
		return -1;
	}
}































