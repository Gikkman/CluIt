package com.cluit.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptException;

import com.cluit.main.javascript.JavascriptEngine;
import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.ReferencePasser;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.structures.Space;

public class Overseer extends Thread {
	public static enum Message {INIT_MESSAGE, CLUSTER_MESSAGE, TERMINATE_MESSAGE };
	
	private final BlockingQueue<Message> mQueue = new LinkedBlockingQueue<>();
	
	private boolean			 mIsAlive = true;
	private File 			 mJsFile;
	private JavascriptEngine mJavascriptEngine;
	
	public Overseer() {
		this.setDaemon(true);
		mJsFile = (File) VariableSingleton.getInstance().getObject(Const.V_KEY_COMBOBOX_JAVASCRIPT_FILE);
	}
	
	@Override
	public void run() {
		while( mIsAlive ){
			try {
				Message message = mQueue.poll(1, TimeUnit.MINUTES);
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
			break;
		case TERMINATE_MESSAGE:
			doTerminate();
			break;
		}
	}

	private void doInit(){
		try {
			mJavascriptEngine = new JavascriptEngine(mJsFile);
			mJavascriptEngine.addCustomFields();
			
		} catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {		
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Overseer couldn't be initialized correctly", e );
		}
	}
	
	private void doCluster(){
		//TODO: Read data from Excel file - http://stackoverflow.com/questions/1516144/how-to-read-and-write-excel-file-in-java
		
		//Read data points				
		Entry[] data = com.cluit.util.methods.MiscUtils.pointsFromBmp();
		int dimensions = data[0].getDimensions();	
		ReferencePasser.storeReference(	Const.REFERENCE_API_SPACE, Space.create(dimensions, data) );
		
		//Create methods that'll be called upon algorithm finish
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_STEP,   (args) -> clusteringStep(args) );
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_FINISH,(args) -> clusteringFinished(args) );
		
		mJavascriptEngine.performClustering();

		MethodMapper.invoke(Const.METHOD_DONE_BUTTON_REACTIVATE);
	}
	
	private void doTerminate(){
		mIsAlive = false;
	}

	private void clusteringFinished(Object ... args) {
		Entry[]  entries = (Entry[]) args[0];
		int[] membership = (int[])   args[1];
		if( membership == null || entries == null )
			return;
		
		paint(entries, membership);
		
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
		com.cluit.util.methods.MiscUtils.colorPixels(entries, membership);
	}
}
