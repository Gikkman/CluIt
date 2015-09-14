package com.cluit.util.dataTypes;

import java.util.ArrayList;
import java.util.HashMap;

import com.cluit.util.structures.Pair;

public class Results {
	private Space mResultSpace;
	private Data  mInputData;
	private HashMap<String, Double> mMiscData;
	
	public Results(Data inputData, Space resultSpace, HashMap<String, Double> misc){
		mResultSpace =  resultSpace;
		mInputData = inputData;
		mMiscData = misc;
	}

	public int numberOfClusters() {
		return mResultSpace.getNumberOfClusters();
	}

	public String[] getLabels() {
		return mInputData.getLabels();
	}

	public double[] getCentoid(int cluster){
		mResultSpace.updateCentoids();
		return mResultSpace.clusterCentoid(cluster).getCoordinates();
	}
	
	public boolean hasMiscData(){
		return mMiscData.size() > 0;
	}
	
	public ArrayList<Pair<String, Double>> getMiscData(){
		ArrayList<Pair<String, Double>> out = new ArrayList<Pair<String, Double>>();
		
		for( String key : mMiscData.keySet() )
			out.add( new Pair<String, Double>(key, mMiscData.get(key) ) );
		
		return out;
	}
	
	public double[] getSquaredErrors(){
		double[] out = new double[ mResultSpace.getNumberOfClusters() ];
		
		for( int i = 0; i < out.length; i++ ){
			out [i] = mResultSpace.getSquaredError(i);
		}
		
		return out;
	}
}
