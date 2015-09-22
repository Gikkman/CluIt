package com.cluit.util.AoP;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.cluit.util.dataTypes.Data;
import com.cluit.util.dataTypes.Results;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.structures.TypedObservableObjectWrapper;

public class VariableSingleton {
	private static final VariableSingleton INSTANCE = new VariableSingleton();
	
	private final Map<String, Integer> 	mIntMap 	= new HashMap<String, Integer>();
	private final Map<String, Double> 	mDobuleMap 	= new HashMap<String, Double> ();
	private final Map<String, Boolean> 	mBoolMap 	= new HashMap<String, Boolean>();
	private final Map<String, String> 	mStringMap 	= new HashMap<String, String> ();
	private final Map<String, Object> 	mObjectMap 	= new HashMap<String, Object> ();

	private TypedObservableObjectWrapper<Space>  mObservableSpace  = new TypedObservableObjectWrapper<Space> (null);
	private TypedObservableObjectWrapper<Data>   mObservableData   = new TypedObservableObjectWrapper<Data>  (null);
	private TypedObservableObjectWrapper<Results> mObservableResult= new TypedObservableObjectWrapper<Results>(null);
	
	public static VariableSingleton getInstance(){		
		return INSTANCE;
	}
	
	/**Returns the value mapped to key (or Integer.MIN_VALUE, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public synchronized int getInt(String key){
		if( mIntMap.containsKey(key) )
			return mIntMap.get(key);
		return Integer.MIN_VALUE;
	}
	
	/**Returns the value mapped to key (or Double.NEGATIVE_INFINITY, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public synchronized double getDouble(String key){
		if( mDobuleMap.containsKey(key) )
			return mDobuleMap.get(key);
		return Double.NEGATIVE_INFINITY;
	}
	
	/**Returns the value mapped to key (or false, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public synchronized boolean getBool(String key){
		if( mBoolMap.containsKey(key) )
			return mBoolMap.get(key);
		return false;
	}
	
	/**Returns the value mapped to key (or NULL, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public synchronized String getString(String key){
		return mStringMap.get(key);
	}
	
	/**Returns the value mapped to key (or NULL, if key is not mapped to anything)
	 * 
	 * @param key
	 */
	public synchronized Object getObject(String key){
		return mObjectMap.get(key);		
	}
	
	public synchronized void putInt(String key, int value){
		mIntMap.put(key, value);
	}
	
	public synchronized void putDouble(String key, double value){
		mDobuleMap.put(key, value);
	}
	
	public synchronized void putBool(String key, boolean value){
		mBoolMap.put(key, value);
	}
	
	public synchronized void putString(String key, String value){
		mStringMap.put(key, value);
	}
	
	public synchronized void putObject(String key, Object value){
		mObjectMap.put(key, value);
	}	
	
	/*********************** SPACE ****************************/
	

	public synchronized void setSpace(Space space){
		mObservableSpace.setValue(space);
	}
	
	public synchronized Space getSpace(){
		return mObservableSpace.getValue();
	}
	
	@SuppressWarnings("unused")
	private synchronized void setSpaceListener(PropertyChangeListener listener){
		mObservableSpace.addPropertyChangeListener(listener);
	}
	
	@SuppressWarnings("unused")
	private synchronized void removeSpaceListener(PropertyChangeListener listener){
		mObservableSpace.removePropertyChangeListener(listener);
	}
	
	/*********************** DATA **********************************/
	
	public synchronized void setData(Data data){
		mObservableData.setValue(data);
	}
	
	public synchronized Data getData(){
		return mObservableData.getValue();
	}
	
	public synchronized void setDataListener(PropertyChangeListener listener){
		mObservableData.addPropertyChangeListener(listener);
	}
	
	public synchronized void removeDataListener(PropertyChangeListener listener){
		mObservableData.removePropertyChangeListener(listener);
	}
	
	/*********************** RESULT **********************************/
	
	public synchronized void setResults(Results results){
		mObservableResult.setValue(results);
	}
	
	public synchronized Results getResults(){
		return mObservableResult.getValue();
	}
	
	public synchronized void setResultListener(PropertyChangeListener listener){
		mObservableResult.addPropertyChangeListener(listener);
	}
	
	public synchronized void removeResultListener(PropertyChangeListener listener){
		mObservableResult.removePropertyChangeListener(listener);
	}
}
