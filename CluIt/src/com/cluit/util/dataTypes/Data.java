package com.cluit.util.dataTypes;

public class Data {
	private String[]   mLabels;
	private double[][] mData;
	
	public Data(String[] labels, double[][] data){
		mLabels = labels;
		mData   = data;
	}
	
	public String[]   getLabels(){ return mLabels; }
	public double[][] getDoublesMatrix()  { return mData;   }
	
}
