package com.cluit.util.dataTypes;

import com.cluit.util.methods.ClusteringUtils;

/**This class holds all data needed to perform a clustering experiment.
 * 
 * With exception for the getNormalizedData method, this class only consists of simple getters and setters.
 * 
 * @author Simon
 *
 */
public class Data {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************

	private String[]   mLabels;
	private double[][] mData;
	private double[][] mNormalizedData;
	
	private String[]   mReferenceLabels;
	private double[][] mReferenceData;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	public Data(String[] labels, double[][] data){
		mLabels = labels;
		mData   = data;
	}
	
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************

	public int getNumberOfEntries(){
		return mData.length;
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

	public String[]   getLabels(){ return mLabels; }
	public double[][] getData()  { return mData;   }
	
	public double[][] getNormalizedData() {
		if( mNormalizedData != null )
			return mNormalizedData;
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
		mNormalizedData = ClusteringUtils.transposeMatrix(transponate);
		return mNormalizedData;
	}

	public double[] getEntryData(int entry){
		return mData[ entry ];
	}
	public double[] getNormalizedEntryData( int entry ){
		return getNormalizedData()[ entry ];
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
