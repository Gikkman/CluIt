package com.cluit.util.dataTypes;

import com.cluit.util.methods.ClusteringUtils;

public class Data {
	private String[]   mLabels;
	private double[][] mData;
	
	private String[]   mReferenceLabels = {"Hello", "Shmoo"};
	private double[][] mReferenceData = { {1.0 , 5.5}, {2.0}, {3.0}, {4.0}, {5.0}, {6.0} };
	
	public Data(String[] labels, double[][] data){
		mLabels = labels;
		mData   = data;
	}
	
	public boolean hasReferenceData() {
		return mReferenceLabels != null && mReferenceData != null;
	}
	
	public void setReferenceData(String[] labels, double[][] referenceData){
		mReferenceLabels = labels;
		mReferenceData = referenceData;
	}
	
	
	
	public String[] getReferenceLabels(){ return mReferenceLabels; }
	public double[][] getReferenceData(){ return mReferenceData; }
	
	public int getNumberOfEntries(){
		return mData.length;
	}
	
	public double[] getEntry(int entry){
		return mData[ entry ];
	}
	
	public String[]   getLabels(){ return mLabels; }
	public double[][] getData()  { return mData;   }
	
	public double[][] getNormalizedData() {
		/* The data is sorted in feature vectors (each row represents an item, each culumn a feature in the items)
		 * However, we want to normalize the feature columns, to get all values for a given feature between 0 and 1.
		 * To do that, we must first transpose the matrix so we can access the columns as though they were rows.
		 * 
		 * Example:
		 * 
		 * x1 y1 z1
		 * x2 y2 z2
		 * x3 y3 z3		<- We can only acess rows, thus we cannot normalize the x - column.
		 * 				 - So, we transpose the matrix
		 * 
		 * x1 x2 x3
		 * y1 y2 y3
		 * z1 z2 z3		<- Now, we can acess each row and normalize it
		 */		
		double[][] transponate = ClusteringUtils.transposeMatrix(mData);
		for(int i = 0; i < transponate.length; i++)
			transponate[i] = ClusteringUtils.normalize( transponate[i] );
		return ClusteringUtils.transposeMatrix(transponate);
	}
}
