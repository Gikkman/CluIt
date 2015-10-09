package com.cluit.util.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;

import com.cluit.util.methods.MiscUtils;
import com.cluit.util.structures.Pair;

public class Results {
	private Space mResultSpace;
	private Data  mInputData;
	private HashMap<String, Double> mMiscData;
	
	private Entry[]  centoidCache = null;
	private double[] squaredErrorCache = null;
	
	public Results(Data inputData, Space resultSpace, HashMap<String, Double> misc){
		mResultSpace =  resultSpace;
		mInputData = inputData;
		mMiscData = misc;
		
		
		calculateCentoidCache();
		calculateSquaredErrorCache();
	}

	public int getNumberOfClusters() {
		return mResultSpace.getNumberOfClusters();
	}
	
	public int getNumberOfFeatures() {
		return mResultSpace.getDimensions();
	}

	public String[] getLabels() {
		return mInputData.getLabels();
	}

	public boolean hasReferenceData(){
		return mInputData.hasReferenceData();
	}
	
	public int referenceDataAmount(){
		return mInputData.getReferenceLabels().length;
	}
	
	public String[] getReferenceLabels(){
		return mInputData.getReferenceLabels();
	}
	
	/**For each cluster, fetch the mean for each reference label
	 * 
	 *  
	 * @return A double matrix, ordered [cluster][label]
	 */
	public double[][] getReferenceDataMeans(){
		double[][] out = new double[ mResultSpace.getNumberOfClusters()][ referenceDataAmount() ];	
		double[][] inData = mInputData.getReferenceData();
		for(int i = 0; i < mResultSpace.getNumberOfClusters(); i++){ 
			int[] entryIDs = getEntryIDsFromCluster(i);
			
			for(int j = 0; j < referenceDataAmount(); j++ ){
				
				for(int id : entryIDs){
					out[i][j] += inData[id][j];
				}
				
				out[i][j] /= entryIDs.length;
				
			}
		}
		return out;
	}	
	
	public boolean hasMiscData(){
		return mMiscData.size() > 0;
	}
	
	/**Fetches a collection of String - Double pairs. These pairs are set by the user within JS files
	 * 
	 * @return
	 */
	public ArrayList<Pair<String, Double>> getMiscData(){
		ArrayList<Pair<String, Double>> out = new ArrayList<Pair<String, Double>>();
		
		for( String key : mMiscData.keySet() )
			out.add( new Pair<String, Double>(key, mMiscData.get(key) ) );
		
		return out;
	}
	
	/**Fetches the centoid for a given cluster
	 * 
	 * @param cluster
	 * @return
	 */
	public double[] getCentoid(int cluster){
		return centoidCache[cluster].getCoordinates();
	}
	
	/**Fetches the squared error for a given cluster
	 * 
	 * @param cluster
	 * @return
	 */
	public double getSquaredError(int cluster){
		return squaredErrorCache[cluster];
	}
	
	/**Fetches the squared errors for all clusters
	 * 
	 * @return An ordered array of doubles, where element i represents the squared error for cluster i
	 */
	public double[] getSquaredErrors(){
		return squaredErrorCache;
	}
	
	public double[][] getInputData(){
		return mInputData.getData();
	}
	
	public double[][] getReferenceData(){
		return mInputData.getReferenceData();
	}
	
	public double[][] getNormalizedClusterEntryValues(int cluster){
		Entry[] entries  = getNormalizedEntries(cluster);
		
		double[][] out = new double[entries.length][mResultSpace.getDimensions()];
		for( int i = 0; i < entries.length; i++){
			out[i] = entries[i].getCoordinates();
		}
				
		return out;
		
	}
	//*******************************************************************************************************
	//region								PRIVATE			
	//*******************************************************************************************************

	/* Since there is a chance that the result space is normalized, we have to "denormalize" all the data from the result space.
	 * 
	 * Therefore, this class does a lot of calculations by extracting entry membership from the Space, and then fetching the
	 * original entries from the Data and performs calculations of those original entries.
	 * 
	 * Several of the calculated values are cached, since they might be used several times (and they are relatively small) 
	 */	
	private void calculateCentoidCache() {
		centoidCache = new Entry[ mResultSpace.getNumberOfClusters() ];
		for( int i = 0; i < mResultSpace.getNumberOfClusters(); i++){	
			Entry[] unnormalizedEntries = getUnnormalizedEntries(i);
			centoidCache[i] = Cluster.calculateCentoid( unnormalizedEntries );
		}		
	}
	
	private void calculateSquaredErrorCache() {
		squaredErrorCache = new double[ mResultSpace.getNumberOfClusters() ];		
		for( int i = 0; i < mResultSpace.getNumberOfClusters(); i++ ){
			Entry[] unnormalizedEntries = getUnnormalizedEntries(i);
			squaredErrorCache [i] = Cluster.calculateSquaredError(centoidCache[i], unnormalizedEntries);
		}
	}
	
	private Entry[] getUnnormalizedEntries(int fromCluster){
		int[] clusterIDs = getEntryIDsFromCluster(fromCluster);
		
		double[][] coords = new double[ clusterIDs.length ][ mResultSpace.getDimensions() ];
		for(int j = 0; j < coords.length; j++){
			coords[j] = mInputData.getEntryData( clusterIDs[j] );
		}			
		return MiscUtils.entriesFromFeatureMatrix(coords);
	}
	
	private Entry[] getNormalizedEntries(int fromCluster){
		int[] clusterIDs = getEntryIDsFromCluster(fromCluster);
		
		double[][] coords = new double[ clusterIDs.length ][ mResultSpace.getDimensions() ];
		for( int j = 0; j < coords.length; j++ ){
			coords[j] = mInputData.getNormalizedEntryData( clusterIDs[j] );
		}
		
		return MiscUtils.entriesFromFeatureMatrix(coords);
	}
	
	private int[] getEntryIDsFromCluster(int cluster){
		Entry[] entries = mResultSpace.getEntriesInCluster(cluster);
		int[] out = new int[ entries.length ];
		
		for(int i = 0; i < out.length; i++){
			out[i] = entries[i].getID();
		}
		
		return out;
	}

	//endregion *********************************************************************************************
	//*******************************************************************************************************

}
