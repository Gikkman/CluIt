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
import com.cluit.util.dataTypes.Results;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.methods.MiscUtils;

public class ClusteringEngine extends Thread {
	public static enum Message {INIT_MESSAGE, CLUSTER_MESSAGE, TERMINATE_MESSAGE };
	
	private final BlockingQueue<Message> mQueue = new LinkedBlockingQueue<>();
	
	private boolean			 mIsAlive = true;
	private File 			 mJsFile;
	private long 			 mJsFileEditedDate = 0;
	private JavascriptEngine mJavascriptEngine;
	
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
		//Error handling, make sure that the JS file is intact
		if( !mJsFile.exists() || !mJsFile.isFile() ){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "The Javascript file "+mJsFile+" could not be evaluated!" + new IOException() );
			return;
		}
		if( mJsFile.lastModified() != mJsFileEditedDate )
			doInit();
		
		//Fetch the data that should have been imported. First, check if something exists, then cast it to Data
		Data data = VariableSingleton.getInstance().getData();
		if( data == null ){
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, "Data from an Excel file must be loaded before clustering can be performed.\nThe Import view can be found under Show -> Excel Import");
			return;
		}
		
		//Create the space, store the space reference for the API and the data reference for the result
		Entry[] entries = MiscUtils.entriesFromFeatureMatrix( (double[][]) data.getDoublesMatrix() );
		int dimensions = entries[0].getDimensions();	
		
		VariableSingleton.getInstance().setSpace( Space.create(dimensions, entries) );
				
		//Create methods that'll be called upon algorithm step and finish
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_STEP,   (args) -> clusteringStep(args) );
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_FINISH,(args) -> clusteringFinished(args) );
		
		mJavascriptEngine.performClustering();
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
		Data  data  =  VariableSingleton.getInstance().getData();
		
		Entry[] entries = space.getAllEntries();
		int[] memberships = new int[entries.length];
		
		for(int i = 0; i < memberships.length; i++){
			int membership = space.getEntryMembership(entries[i]);
			if( membership == -1)
				System.out.println("Entry "+i+" is not clustered, FYI "+com.cluit.util.methods.MiscUtils.getStackPos());
			else
				memberships[i] = membership; 
		}
		
		paint(entries, memberships);
		
		//Sets the results of this clustering. This'll fire all Results listeners
		Results results = new Results(data, space, map);
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
	
	private void paint(Entry[] entries, int[] membership){
		try { 
			MiscUtils.colorPixels(entries, membership);
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
