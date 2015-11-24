package com.cluit.util.dataTypes;

import java.util.Arrays;

/**Base class for a data entry. An entry consists of an array of doubles, which represents the entry's coordinates in a space.
 * 
 * @author Simon
 *
 */
public class Entry {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************

	private final double[] coords;
	private final int ID;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	/**Constructor for creating entries which don't represent elements in the object space. Primarily for representing cluster centoids.
	 * 
	 * @param coordinates
	 */
	public Entry(double ... coordinates) {
		this(-1, coordinates);
	}
	
	/**Creates a new Entry with the given coordinates. If the entry is not derived from an element (ie, is a cluster centoid), the ID can be set to anything
	 * 
	 * @param ID  This entries ID (ie, which element from the element collection was this entry generated from) 
	 * @param coordinates This entries different features (will be used as coordiantes in the data space)
	 */
	public Entry(int id, double... coordinates){
		this.ID = id;
		this.coords = coordinates;
	}
	
	/**Copy constructor.<br> 
	 * Creates a new Entry that's a copy of the argument entry. This constructor utilizes deep copying to make sure that the new Entry
	 * has a unique internal coordinate-array
	 * 
	 * @param e
	 */
	public Entry(Entry e) {
		this.ID = e.getID();
		this.coords = Arrays.copyOf(e.getCoordinates(), e.getDimensions());
	}
	
	//endregion *********************************************************************************************
	//region								STATIC			
	//*******************************************************************************************************

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
	
	public final int getID(){
		return ID;
	}
	
	@Override
	public String toString(){
		String out = "(";
		for(double d:coords)
			out+=d+" , ";
		out = out.length() > 3 ? out.substring(0, out.length()-3 ) : "( ";
		return out + ")";
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
