package com.cluit.util.AoP;

import javafx.scene.paint.Color;

import java.beans.PropertyChangeListener;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.cluit.util.dataTypes.Data;
import com.cluit.util.dataTypes.ExperimentBundle;
import com.cluit.util.dataTypes.Results;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.preferences.PreferenceManager;
import com.cluit.util.structures.TypedObservableObjectWrapper;

public class VariableSingleton {
	private static final VariableSingleton INSTANCE = new VariableSingleton();
	
	private final Map<String, Integer> 	mIntMap 	= new HashMap<String, Integer>();
	private final Map<String, Double> 	mDobuleMap 	= new HashMap<String, Double> ();
	private final Map<String, Boolean> 	mBoolMap 	= new HashMap<String, Boolean>();
	private final Map<String, String> 	mStringMap 	= new HashMap<String, String> ();
	private final Map<String, Object> 	mObjectMap 	= new HashMap<String, Object> ();
	
	private final Map<String, TypedObservableObjectWrapper<Color>>	mColorMap		= new HashMap<>();
	private final Map<Integer,TypedObservableObjectWrapper<String>>	mClusterNameMap = new HashMap<>();
	
	private final Map<String, Object>   mUserDefinedVariables = new HashMap<>();
		
	private TypedObservableObjectWrapper<Space>  mObservableSpace  = new TypedObservableObjectWrapper<Space> (null);
	private TypedObservableObjectWrapper<Data>   mObservableData   = new TypedObservableObjectWrapper<Data>  (null);
	private TypedObservableObjectWrapper<Results> mObservableResult= new TypedObservableObjectWrapper<Results>(null);
	
	private Deque<ExperimentBundle> mExperimentQueue = new LinkedList<>();
	
	public static VariableSingleton getInstance(){		
		return INSTANCE;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//region							GETTING		
	////////////////////////////////////////////////////////////////////////////////////	
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
	
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							PUTTING		
	////////////////////////////////////////////////////////////////////////////////////
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
	
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							SPACE		
	////////////////////////////////////////////////////////////////////////////////////
	

	public synchronized void setSpace(Space space){
		mObservableSpace.setValue(space);
	}
	
	public synchronized Space getSpace(){
		return mObservableSpace.getValue();
	}
	
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							DATA		
	////////////////////////////////////////////////////////////////////////////////////
	
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
	
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							RESULT		
	////////////////////////////////////////////////////////////////////////////////////
	
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
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							ExperimentQueue	
	////////////////////////////////////////////////////////////////////////////////////
	
	public synchronized void enqueueExperiment(ExperimentBundle bundle){
		mExperimentQueue.addLast(bundle);
	}
	
	public synchronized void enqueueExperiment_FRONT(ExperimentBundle bundle){
		mExperimentQueue.addFirst(bundle);
	}
	
	
	public synchronized ExperimentBundle peekNextExperimentBundle(){
		return mExperimentQueue.peek();
	}
	
	public synchronized ExperimentBundle pollNextExperimentBundle(){
		return mExperimentQueue.poll();
	}
	
	public synchronized int getExperimentQueueSize(){
		return mExperimentQueue.size();
	}
	
	
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							User Defined Variable Fields		
	////////////////////////////////////////////////////////////////////////////////////
	public void putUserDefinedValue(String key, Object object){
		mUserDefinedVariables.put(key, object);
	}
	
	 /** 
	 * @return A copy of the User Defined Map (the one that stores the JS defined variable fields)
	 */
	public Map<String, Object> getUserDefinedMap(){
		return new HashMap<>( mUserDefinedVariables );
	}
	
	public void clearUserDefinedMap(){
		mUserDefinedVariables.clear();
	}
	//endregion/////////////////////////////////////////////////////////////////////////
	//
	//region							Visualization variables			
	////////////////////////////////////////////////////////////////////////////////////
	/**Fetches the color for a given feature. This color is wrapped to make it observable. A class can add a 
	 * <code>PropertyChangeListener</code> to the returned object.
	 * <br><br>
	 * Changes to the color persists throughout sessions. This is realized via that the object has a pre-attached change listener, 
	 * which will update the associated user preferences
	 * 
	 * @param feature Name of the feature whose color you seek
	 * @return
	 */
	public TypedObservableObjectWrapper<Color> getFeatureColor(String feature){
		if( !mColorMap.containsKey(feature) ) {
			Color color = PreferenceManager.getFeatureColor(feature);
			
			TypedObservableObjectWrapper<Color> observableColor = new TypedObservableObjectWrapper<Color>(color);
			observableColor.addPropertyChangeListener( (event) -> PreferenceManager.setFeatureColor(feature, (Color) event.getNewValue()) );
			
			mColorMap.put(feature, observableColor);
		}
		
		return mColorMap.get(feature);
	}

	/**Fetches the name for a given cluster index. This name is wrapped to make it observable. A class can add a 
	 * <code>PropertyChangeListener</code> to the returned object.
	 * <br><br>
	 * Changes to the wrapped name persists throughout sessions. This is realized via that the object has a pre-attached change listener, 
	 * which will update the associated user preferences
	 * 
	 * @param index Index of the cluster whose name you seek
	 * @return
	 */
	public TypedObservableObjectWrapper<String> getClusterName(int index){
		if( !mClusterNameMap.containsKey(index) ){
			String name = PreferenceManager.getClusterName(index);
			
			TypedObservableObjectWrapper<String> observableName = new TypedObservableObjectWrapper<String>(name);
			observableName.addPropertyChangeListener( (event) -> PreferenceManager.setClusterName(index, (String) event.getNewValue() ) );
			
			mClusterNameMap.put(index, observableName);
		}
		
		return mClusterNameMap.get(index);
	}
	////////////////////////////////////////////////////////////////////////////////////
	//endregion
	//
}
