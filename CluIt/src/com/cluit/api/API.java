package com.cluit.api;

import java.util.HashMap;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.ReferencePasser;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.structures.KeyPriorityQueue_Max;
import com.cluit.util.structures.KeyPriorityQueue_Min;
import com.cluit.util.structures.Space;

/**This class is intended to be used as the API between CluIt and Javascript. All communication with the clustering space goes via an instance
 * of this class. This class also handles the "Finished" function, which the Javascript can call to signal that the algorithm is done.
 * 
 * When the class is construct, it fetches a reference to the Space-object. It will then relay all Javascript calls to the Space.
 * 
 * @author Simon
 *
 */
public class API {
	private Space mSpace;
	private HashMap<String, Double> mMiscData = new HashMap<>();
	
	private API() {
		mSpace = ReferencePasser.getReference(Const.REFERENCE_API_SPACE);
		if( mSpace == null ) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "API couldn't fetch a viable clustering algorithm object", new Exception() );
		}
	}
	
	public static API create_this_API(){
		return new API();	
	}
	
	//Step is intended to be used for agglomerative functions, to signal that the current clustering structure is considered one step "forward"
	//from the initial cluster layout.
	//TODO: Add support for the API.step() method
	public void step(){
		/*
		Entry[] entries = mSpace.getAllEntries();
		int[] memberships = new int[entries.length];		
		for(int i = 0; i < memberships.length; i++)
			memberships[i] = mSpace.getEntryMembership(entries[i]);
		
		MethodMapper.invoke(Const.METHOD_JS_SCRIPT_STEP, entries, memberships );
		*/
	}
	/**Finalized the clustering by calling the Finish-method in the Overseer-class, and passing the membership array as an argument*/
	public void finish(){	
		mSpace.updateCentoids();
		
		MethodMapper.invoke(Const.METHOD_JS_SCRIPT_FINISH, mSpace, mMiscData );
	}
	/**Debug method. If API.test() is called by Javascript, "Test completed" is printed into the console  */
	public void test(){
		System.out.println("Test completed");
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//																				  //
	//                             All the API Functions                              //
	//               					    |                                         //
	//                						|                                         //
	//                						V                                         //
	////////////////////////////////////////////////////////////////////////////////////
	public int addCluster(double ... coordinates){
		return mSpace.addCluster(coordinates);
	}
	
	public int addEntryToCluster(Entry e, int cluster){
		return mSpace.addEntryToCluster(e, cluster);
	}
	
	public int getTargetNumberOfClusters(){
		return VariableSingleton.getInstance().getInt(Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS);
	}
	
	public int getCurrentNumberOfClusters(){
		return mSpace.getNumberOfClusters();
	}
	
	public int getNumberOfEntries(){
		return mSpace.getNumberOfEntries();
	}
	
	public Entry[] getAllEntries(){
		return mSpace.getAllEntries();
	}
	
	public Entry[] getFreeEntries(){
		return mSpace.getFreeEntries();
	}
	
	public Entry[] getClusteredEntries(){
		return mSpace.getCludEntried();
	}
	
	public boolean isClustered(Entry e){
		return mSpace.isClustered(e);
	}
	
	public int getEntryMembership(Entry e){
		return mSpace.getEntryMembership(e);
	}
	
	public double getDistance(Entry e1, Entry e2){
		return mSpace.getDistance(e1, e2);
	}
	
	public double getDistance(int c1, int c2){
		return mSpace.getDistance(c1, c2);
	}		
	
	public <T> T[] shuffleArray(T[] t){
		com.cluit.util.methods.ClusteringUtils.fyShuffle(t);
		return t;
	}
	
	public KeyPriorityQueue_Max<Entry> createEntryPriorityQueue_Max(){
		return new KeyPriorityQueue_Max<Entry>();
	}
	
	public KeyPriorityQueue_Min<Entry> createEntryPriorityQueue_Min(){
		return new KeyPriorityQueue_Min<Entry>();
	}
	
	public Object getFieldValue(String name){
		return VariableSingleton.getInstance().getObject(Const.V_KEY_JS_REFERENCE+name);
	}
	
	public void addMiscData(String ID, double value){
		mMiscData.put(ID, value);
	}
}
