package com.cluit.api;

import java.util.Random;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.ReferencePasser;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.structures.KeyPriorityQueue_Max;
import com.cluit.util.structures.KeyPriorityQueue_Min;
import com.cluit.util.structures.Space;


public class API {
	private Space mSpace;
	
	private API() {
		mSpace = ReferencePasser.getReference(Const.REFERENCE_API_SPACE);
		if( mSpace == null ) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "API couldn't fetch a viable clustering algorithm object", new Exception() );
		}
	}
	
	public static API create(){
		API api = new API();			
		return api;
	}
	
	//TODO: Fake a "delayed" queue
	public void step(){
		Entry[] entries = mSpace.getAllEntries();
		int[] memberships = new int[entries.length];
		
		for(int i = 0; i < memberships.length; i++)
			memberships[i] = mSpace.getEntryMembership(entries[i]);
		
		MethodMapper.invoke(Const.METHOD_JS_SCRIPT_STEP, entries, memberships );
	}
	
	public void finalize(){
		Entry[] entries = mSpace.getAllEntries();
		int[] memberships = new int[entries.length];
		
		for(int i = 0; i < memberships.length; i++){
			int membership = mSpace.getEntryMembership(entries[i]);
			if( membership == -1)
				System.out.println("Entry "+i+" is not clustered, FYI "+com.cluit.util.methods.MiscUtils.getStackPos());
			else
				memberships[i] = membership; 
		}
		
		MethodMapper.invoke(Const.METHOD_JS_SCRIPT_FINISH, entries, memberships );
	}
	
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
		com.cluit.util.methods.ClusteringUtils.fyShuffle(t, new Random() );
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
	
	public void createField_IntegerSpinner(String name, int ... values){
		int[] defaults = {0, 100, 5, 1};
		Object[] args = new Object[5];
		args[0] = name;		
		
		for(int i = 0; i < 4; i++){
			if( values.length > i)
				args[i+1] = values[i];
			else
				args[i+1] = defaults[i];
		}
		
		MethodMapper.invoke(Const.METHOD_ADD_INTEGER_SPINNER, args);
	}

	public void createField_CheckBox(String name, boolean ... values){
		Object[] args = new Object[2];
		args[0] = name;
		args[1] = (values.length > 0) ? values[0] : false;
		
		MethodMapper.invoke(Const.METHOD_ADD_CHECKBOX, args);
	}
}
