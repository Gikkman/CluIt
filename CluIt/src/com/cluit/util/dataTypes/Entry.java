package com.cluit.util.dataTypes;

import java.util.Arrays;

public class Entry {
	
private final double[] coords;
	
	public Entry(double... coordinates){
		coords = coordinates;
	}
	
	public Entry(Entry p) {
		coords = Arrays.copyOf(p.getAllEntries(), p.getDimensions());
	}

	public double getEntry( int dimension ){
		return coords[dimension];
	}
	
	public double[] getAllEntries(){
		return coords;
	}
	
	public int getDimensions(){
		return coords.length;
	}
	
	@Override
	public String toString(){
		String out = "(";
		for(double d:coords)
			out+=d+" , ";
		out = out.length() > 3 ? out.substring(0, out.length()-3 ) : "( ";
		return out + ")";
	}
}
