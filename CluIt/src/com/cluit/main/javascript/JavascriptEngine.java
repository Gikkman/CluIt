package com.cluit.main.javascript;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;

import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

public class JavascriptEngine {
	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************
	
	private final ScriptEngine mScriptEngine;
	private final String	   mFieldsFunctionName;
	private final String 	   mEntryFunctionName;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	/**Sets up a Javascript engine, bound to the file specified by <b>jsFilePath</b>.
	 * 
	 * @param jsFilePath
	 * @throws FileNotFoundException In case the .js file is not found in the specified path
	 * @throws ScriptException In case the .js file wasn't evaluated correctly
	 * @throws NoSuchMethodException  In the case the .js file doesn't have the method specified by jsEntryFunction
	 */
	public JavascriptEngine(File jsFile) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		mEntryFunctionName = Const.STRING_JAVASCRIPT_ENTRY_FUNCTION;
		mFieldsFunctionName =Const.STRING_JAVASCRIPT_ADD_FIELDS_FUNCTION;
		
		//Set up the Nashorn script engine and then evaluate the script and whether the entry function exists
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		mScriptEngine = factory.getScriptEngine( new com.cluit.main.javascript.AllowedClassesFilter() );
		
		
		mScriptEngine.eval( new FileReader( jsFile ) );
		
		if (mScriptEngine.eval( 
				"if (typeof "+mEntryFunctionName+" == 'function') "
						+ "{ var JS_ENGINE_TRUE = function() { return true; };  JS_ENGINE_TRUE();  } "
			  + "else "
			  			+ "{ var JS_ENGINE_FALSE= function() { return false; }; JS_ENGINE_FALSE(); } "
			).equals(false) ) {
			throw new NoSuchMethodException("No entry method! " + mEntryFunctionName +" not found in script definition: "+ jsFile); 
		}
		
		if (mScriptEngine.eval( 
				"if (typeof "+mFieldsFunctionName+" == 'function') "
						+ "{ var JS_ENGINE_TRUE = function() { return true; };  JS_ENGINE_TRUE();  } "
			  + "else "
			  			+ "{ var JS_ENGINE_FALSE= function() { return false; }; JS_ENGINE_FALSE(); } "
			).equals(false) ) {
			throw new NoSuchMethodException("No custom fields method! " + mFieldsFunctionName +" not found in script definition: "+ jsFile); 
		}
	}
	
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************

	
	public void performClustering() {	
		
		Invocable invocable = (Invocable) mScriptEngine;
		try {
			//This EVAL statement creates the API object within the script engine, so that JS can call methods from the API class
			mScriptEngine.eval("var API = Java.type(\"com.cluit.api.API\").create_this_API();");		
			//Then, run the .calculate() method defined in JS
			invocable.invokeFunction( mEntryFunctionName );
		} catch (NoSuchMethodException e) {		
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! No such method: " + e.getMessage(), e);
		} catch (ScriptException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! Script exception: " + e.getMessage(), e);
		} catch (Exception e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! Unknown exception: " + e.getMessage(), e);
		}
	}
	
	public void addCustomFields() {
		Invocable invocable = (Invocable) mScriptEngine;		
		try{
			//This EVAL statement creates the JFX_API object within the script engine, so that JS can call methods which adds fields to our
			//controll window
			mScriptEngine.eval("var JFX_API = Java.type(\"com.cluit.api.JFX_API\").create_this_API();");	
			//Run the .fields() method in JS
			invocable.invokeFunction("fields");
		} catch (NoSuchMethodException e) {		
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! No such method: " + e.getMessage(), e);
		} catch (ScriptException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! Script exception: " + e.getMessage(), e);
		} catch (Exception e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_JS, "Javascript Exception occured! Unknown exception: " + e.getMessage(), e);
		}
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
