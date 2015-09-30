package com.cluit.util.dataTypes;

import java.io.File;
import java.util.Map;

public class ExperimentBundle {

	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************
	private File mJsFile;
	private Data mData;
	private Space mSpace;
	private Map<String, Object> mUserDefined;
	
	private boolean mDoNormalize;
	private int	    mNumberOfClusters;
	
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************
	private ExperimentBundle(){};
	//endregion *********************************************************************************************
	//region								STATIC			
	//*******************************************************************************************************
	public static ExperimentBundle create(){
		return new ExperimentBundle();
	}
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************
	public ExperimentBundle setSpace(Space space){
		mSpace = space;
		return this;
	}
	public ExperimentBundle setData(Data data){
		mData = data;
		return this;
	}
	public ExperimentBundle setJavascriptFile(File file){
		mJsFile = file;
		return this;
	}
	public ExperimentBundle setUserDefinedData( Map<String, Object> map){
		mUserDefined = map;
		return this;
	}
	public ExperimentBundle setDoNormalize(boolean doNormalize){
		mDoNormalize = doNormalize;
		return this;
	}
	public ExperimentBundle setNumberOfClusters(int numberOfClusters){
		mNumberOfClusters = numberOfClusters;
		return this;
	}

	public File  getJsFile() {
		return mJsFile;
	}
	public Data  getData() {
		return mData;
	}
	public Space getSpace() {
		return mSpace;
	}
	public boolean  getNormalize() {
		return mDoNormalize;
	}
	public int 		getNumberOfClusters() {
		return mNumberOfClusters;
	}
	public Map<String, Object> getUserDefinedData() {
		return mUserDefined;
	}
	//endregion *********************************************************************************************
	//region								PRIVATE 		
	//*******************************************************************************************************

	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
