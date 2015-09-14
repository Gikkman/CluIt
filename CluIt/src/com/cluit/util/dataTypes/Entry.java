package com.cluit.util.dataTypes;

import java.util.Arrays;

/**Base class for a data entry. An entry consists of an array of doubles, which represents the entry's coordinates in a space.
 * 
 * @author Simon
 *
 */
public class Entry {
	
private final double[] coords;
	
	/**Creates a new Entry with the given coordinates
	 * 
	 * @param coordinates
	 */
	public Entry(double... coordinates){
		coords = coordinates;
	}
	
	/**Copy constructor.<br> 
	 * Creates a new Entry that's a copy of the argument entry. This constructor utilizes deep copying to make sure that the new Entry
	 * has a unique internal coordinate-array
	 * 
	 * @param e
	 */
	public Entry(Entry e) {
		coords = Arrays.copyOf(e.getCoordinates(), e.getDimensions());
	}

	/**Fetches the data at a given coordinate
	 * 
	 * @param dimension
	 * @return
	 */
	public final double getCoordinateAt( int dimension ){
		return coords[dimension];
	}
	
	public final double[] getCoordinates(){
		return coords;
	}
	
	public final int getDimensions(){
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
