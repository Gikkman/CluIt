package com.cluit.util.AoP;

import java.util.HashMap;
import java.util.Map;

public class VariableSingleton {
	private static final VariableSingleton INSTANCE = new VariableSingleton();
	
	private final Map<String, Integer> 	mIntMap 	= new HashMap<String, Integer>();
	private final Map<String, Double> 	mDobuleMap 	= new HashMap<String, Double> ();
	private final Map<String, Boolean> 	mBoolMap 	= new HashMap<String, Boolean>();
	private final Map<String, String> 	mStringMap 	= new HashMap<String, String> ();
	private final Map<String, Object> 	mObjectMap 	= new HashMap<String, Object> ();
	
	public static VariableSingleton getInstance(){
		return INSTANCE;
	}
	
	/**Returns the value mapped to key (or Integer.MIN_VALUE, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public int getInt(String key){
		if( mIntMap.containsKey(key) )
			return mIntMap.get(key);
		return Integer.MIN_VALUE;
	}
	
	/**Returns the value mapped to key (or Double.NEGATIVE_INFINITY, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public double getDouble(String key){
		if( mDobuleMap.containsKey(key) )
			return mDobuleMap.get(key);
		return Double.NEGATIVE_INFINITY;
	}
	
	/**Returns the value mapped to key (or false, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public boolean getBool(String key){
		if( mBoolMap.containsKey(key) )
			return mBoolMap.get(key);
		return false;
	}
	
	/**Returns the value mapped to key (or NULL, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public String getString(String key){
		return mStringMap.get(key);
	}
	
	/**Returns the value mapped to key (or NULL, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public Object getObject(String key){
		return mObjectMap.get(key);		
	}
	
	public void putInt(String key, int value){
		mIntMap.put(key, value);
	}
	
	public void putDouble(String key, double value){
		mDobuleMap.put(key, value);
	}
	
	public void putBool(String key, boolean value){
		mBoolMap.put(key, value);
	}
	
	public void putString(String key, String value){
		mStringMap.put(key, value);
	}
	
	public void putObject(String key, Object value){
		mObjectMap.put(key, value);
	}	
}
