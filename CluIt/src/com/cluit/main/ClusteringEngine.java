package com.cluit.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;

import com.cluit.main.javascript.JavascriptEngine;
import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Data;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.dataTypes.ExperimentBundle;
import com.cluit.util.dataTypes.Results;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.methods.MiscUtils;

public class ClusteringEngine extends Thread {
	public static enum Message {INIT_MESSAGE, RUNQUEUE_MESSAGE, ENQUEUE_MESSAGE, CLUSTER_MESSAGE, TERMINATE_MESSAGE };
	
	private final BlockingQueue<Message> mQueue = new LinkedBlockingQueue<>();
	
	private boolean			 mIsAlive = true;
	private File 			 mJsFile;
	private long 			 mJsFileEditedDate = 0;
	private JavascriptEngine mJavascriptEngine;
	
	private Data mDataCache;
	
	public ClusteringEngine() {
		this.setDaemon(true);
	}
	
	@Override
	public void run() {
		while( mIsAlive ){
			try {
				Message message = mQueue.poll(1, TimeUnit.SECONDS);
				if( message != null ) 
					handleMessage( message );			
			} catch (InterruptedException e) {
				MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The Overseer.fetchMessage() was interupted for an unknown reason", e);
			}
		}
	}
	
	public void queueMessage(Message m){
		try {
			mQueue.put(m);
		} catch (InterruptedException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The Overseer.queueMessage() was interupted for an unknown reason", e);
		}
	}
	
	private void handleMessage(Message message) {
		switch (message) {
		case INIT_MESSAGE:
			doInit();
			break;
		case CLUSTER_MESSAGE:
			doCluster();
			MethodMapper.invoke(Const.METHOD_DONE_BUTTON_REACTIVATE);
			break;
		case TERMINATE_MESSAGE:
			doTerminate();
			break;
		case ENQUEUE_MESSAGE:
			doEnqueueExperiment();
			break;
		case RUNQUEUE_MESSAGE:
			doRunQueue();
			MethodMapper.invoke(Const.METHOD_DONE_BUTTON_REACTIVATE);
			break;
		}
	}

	private void doInit(){
		try {
			mJsFile = (File) VariableSingleton.getInstance().getObject(Const.V_KEY_COMBOBOX_JAVASCRIPT_FILE);
			mJsFileEditedDate = mJsFile.lastModified();
			mJavascriptEngine = new JavascriptEngine(mJsFile);
			
			MethodMapper.invoke(Const.METHOD_CLEAR_TOOLS_PANE);
			mJavascriptEngine.addCustomFields();
			
		} catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {		
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Overseer couldn't be initialized correctly", e );
		}
	}
	
	private void doCluster(){	
		ExperimentBundle bundle = getExperimentBundle();
		if(bundle == null)
			return;
		
		performClustering(bundle);
	}

	private void doEnqueueExperiment(){
		ExperimentBundle bundle = getExperimentBundle();
		if( bundle == null )
			return;
		
		VariableSingleton.getInstance().enqueueExperiment(bundle);
	}
	
	private void doRunQueue(){
		VariableSingleton singleton = VariableSingleton.getInstance();
		while( singleton.getExperimentQueueSize() > 0){
			ExperimentBundle bundle = singleton.pollNextExperimentBundle();
			performClustering(bundle);
		}
	}

	private void doTerminate(){
		mIsAlive = false;
	}

	/**This method is intended to be called when the clustering algorithm is finished.
	 * 
	 * @param args The Space, after all clustering is done
	 */
	@SuppressWarnings("unchecked")
	private void clusteringFinished(Object ... args) {
		Space space = (Space) args[0];
		HashMap<String, Double> map = (HashMap<String, Double> ) args[1];
		
		if( space.getDimensions() == 2)
			paintBmp(space);
		
		//Sets the results of this clustering. This'll fire all Results listeners
		Results results = new Results(mDataCache, space, map);
		VariableSingleton.getInstance().setResults(results);
		
		MethodMapper.removeMethod(Const.METHOD_JS_SCRIPT_STEP);
		MethodMapper.removeMethod(Const.METHOD_JS_SCRIPT_FINISH);
	}
	
	private void clusteringStep(Object ... args) {
		Entry[]  entries = (Entry[]) args[0];
		int[] membership = (int[])   args[1];
		if( membership == null || entries == null )
			return;
		
		paint(entries, membership);
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	//								COLLECT EXPERIMENT BUNDLE	
	////////////////////////////////////////////////////////////////////////////////////
	private ExperimentBundle getExperimentBundle(){
		//Error handling, make sure that the JS file is intact
		if( !mJsFile.exists() || !mJsFile.isFile() ){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "The Javascript file "+mJsFile+" could not be evaluated!" + new IOException() );
			return null;
		}
		if( mJsFile.lastModified() != mJsFileEditedDate )
			doInit();
		
		//Fetch the data that should have been imported and check if something exists and that it is valid
		mDataCache = VariableSingleton.getInstance().getData();
		if( mDataCache == null || mDataCache.getLabels().length == 0 ){
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, "Data from an Excel file must be loaded before clustering can be performed.\nThe Import view can be found under Show -> Excel Import\n\nAnother error might be that no features was chosen for import. Make sure that at least 1 element in the CLUSTER row is selected");
			return null;
		}		
		//Normalize data (if the user wishes it)
		double[][] calcData = null;
		boolean normalized = VariableSingleton.getInstance().getBool( Const.V_KEY_CHECKBOX_NORMALIZE);
		if( normalized ){
			calcData = mDataCache.getNormalizedData();
		} else {
			calcData = mDataCache.getData();
		}
		
		//Create the calculation space
		Entry[] entries = MiscUtils.entriesFromFeatureMatrix( calcData );
		int dimensions = entries[0].getDimensions();	
		Space space = Space.create(normalized, dimensions, entries);
		
		//Lastly, create the experiment bundle object and enqueue it
		ExperimentBundle bundle = ExperimentBundle.create()
									.setData(mDataCache)
									.setJavascriptFile(mJsFile)
									.setSpace(space)
									.setUserDefinedData( VariableSingleton.getInstance().getUserDefinedMap() )
									.setNumberOfClusters( VariableSingleton.getInstance().getInt( Const.V_KEY_SPINNER_NUMBER_OF_CLUSTERS ) );
		return bundle;
	}
	

	private void performClustering(ExperimentBundle bundle) {
		mDataCache = bundle.getData();
		//Create methods that'll be called upon algorithm step and finish
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_STEP,   (args) -> clusteringStep(args) );
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_FINISH,(args) -> clusteringFinished(args) );
		
		//Add the experiment bundle to the FRONT of the experiment queue (So it'll be the first one to be performed)
		VariableSingleton.getInstance().enqueueExperiment_FRONT(bundle);
		
		mJavascriptEngine.performClustering();
	}
		
	////////////////////////////////////////////////////////////////////////////////////
	//region								PAINTING	
	////////////////////////////////////////////////////////////////////////////////////
	private void paintBmp(Space space) {
		Entry[] entries = space.getAllEntries();
		int[] memberships = new int[entries.length];
		
		for(int i = 0; i < memberships.length; i++){
			int membership = space.getEntryMembership(entries[i]);
			if( membership == -1)
				System.out.println("Entry "+i+" is not clustered, FYI "+com.cluit.util.methods.MiscUtils.getStackPos());
			else
				memberships[i] = membership; 
		}
		
		for(int i = 0; i < entries.length; i++){
			entries[i] = new Entry(i, mDataCache.getEntryData(entries[i].getID()));
		}
		
		if( entries[0].getDimensions() == 2)
			paint(entries, memberships);
	}
	
	private void paint(Entry[] entries, int[] membership){
		try { 
			MiscUtils.colorPixels(entries, membership);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	//endregion
	////////////////////////////////////////////////////////////////////////////////////
}
