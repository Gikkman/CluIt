package com.cluit.api;

import java.util.HashMap;
import java.util.Map;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.dataTypes.ExperimentBundle;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.structures.KeyPriorityQueue_Max;
import com.cluit.util.structures.KeyPriorityQueue_Min;

/**This class is intended to be used as the API between CluIt and Javascript. All communication with the clustering space goes via an instance
 * of this class. This class also handles the "Finished" function, which the Javascript can call to signal that the algorithm is done.
 * 
 * When the class is construct, it fetches a reference to the Space-object. It will then relay all Javascript calls to the Space.
 * 
 * @author Simon
 *
 */
public class API {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************
	
	private final int mNumberOfClusters;
	private final Space mSpace;
	private final Map<String, Object> mUserDefinedVariables;

	private HashMap<String, Double> mMiscData = new HashMap<>();
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************
	
	private API() {
		ExperimentBundle bundle = VariableSingleton.getInstance().pollNextExperimentBundle();
		
		mSpace = bundle.getSpace();
		if( mSpace == null ) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "API couldn't fetch a viable clustering space object", new Exception() );
		}
		
		mUserDefinedVariables = bundle.getUserDefinedData();
		mNumberOfClusters = bundle.getNumberOfClusters();
	}
	
	//endregion *********************************************************************************************
	//region								STATIC			
	//*******************************************************************************************************
	
	/**Creates the API object. If this method is intended to be called from within a Nashorn JS-Engine
	 */
	public static API create_this_API(){
		return new API();	
	}
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************	
	
	//Step is intended to be used for agglomerative functions, to signal that the current clustering structure is considered one step "forward"
	//from the initial cluster layout.
	public void step(){
	}
	
	/**Finalized the clustering by calling the Finish-method in the ClusterEngine-class*/
	public void finish(){	
		mSpace.updateCentoids();
		
		MethodMapper.invoke(Const.METHOD_JS_SCRIPT_FINISH, mSpace, mMiscData );
	}
	
	/**Debug method. If API.test() is called by Javascript, "Test completed" is printed into the console  */
	public void test(){
		System.out.println("Test completed");
	}
	//endregion ///////////////////////////////////////////////////////////////////////
	//																				  //
	//region                       All the API Functions                              //
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
		return mNumberOfClusters;
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
		return mSpace.getClusteredEntries();
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
		if( mUserDefinedVariables != null )
			return mUserDefinedVariables.get(name);
		return null;
	}
	
	public double calcSquaredError(int cluster){
		return mSpace.getSquaredError(cluster);
	}
	
	public void addMiscData(String ID, double value){
		mMiscData.put(ID, value);
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
