package com.cluit.util.dataTypes;

import com.cluit.util.structures.Space;

public class Results {
	private Space mResultSpace;
	private Data  mInputData;
	
	public Results(Data inputData, Space resultSpace){
		mResultSpace =  resultSpace;
		mInputData = inputData;
	}

	public int numberOfClusters() {
		return mResultSpace.getNumberOfClusters();
	}

	public String[] getLabels() {
		return mInputData.getLabels();
	}

	public double[] getCentoid(int cluster){
		mResultSpace.updateCentoids();
		return mResultSpace.clusterCentoid(cluster);
	}
}
