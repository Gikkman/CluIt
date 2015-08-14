package com.cluit.main;

import java.io.File;
import java.io.FileNotFoundException;

import javax.script.ScriptException;

import com.cluit.main.javascript.JavascriptEngine;
import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.ReferencePasser;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.structures.Space;

public class Overseer {
	private final JavascriptEngine mJavascriptEngine;
	
	private Overseer() throws FileNotFoundException, ScriptException, NoSuchMethodException {
		//TODO: Read data from Excel file - http://stackoverflow.com/questions/1516144/how-to-read-and-write-excel-file-in-java
		//Create points
		//Create JS Engine
		//Extract settings, such as "which JS to run"
		File jsFile 	       = (File) VariableSingleton.getInstance().getObject(Const.KEY_COMBOBOX_JAVASCRIPT_FILE);
		String jsEntryFunction = Const.STRING_JAVASCRIPT_ENTRY_FUNCTION;	
			
		Entry[] data = com.cluit.util.methods.MiscUtils.pointsFromBmp();
		int dimensions = data[0].getDimensions();
		
		ReferencePasser.storeReference(	Const.REFERENCE_API_SPACE, Space.create(dimensions, data) );
		
		mJavascriptEngine = new JavascriptEngine(jsFile, jsEntryFunction);
	}
	
	public static Overseer create() {
		try {
			return new Overseer();
		} catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {		
			MethodMapper.invoke(Const.EXCEPTION_GENERAL, "Overseer couldn't be instantiated", e );
			return null;
		}	
	}
	
	public void cluster(){
		MethodMapper.addMethod(Const.METOD_JS_SCRIPT_STEP, (args) -> clusteringStep(args) );
		MethodMapper.addMethod(Const.METHOD_JS_SCRIPT_FINISH,(args) -> clusteringFinished(args) );
		
		mJavascriptEngine.start();
	}

	private void clusteringFinished(Object ... args) {
		Entry[]  entries = (Entry[]) args[0];
		int[] membership = (int[])   args[1];
		if( membership == null || entries == null )
			return;
		
		paint(entries, membership);
		
		MethodMapper.invoke(Const.METHOD_DONE_BUTTON_REACTIVATE);
		MethodMapper.removeMethod(Const.METOD_JS_SCRIPT_STEP);
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
