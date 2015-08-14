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

public class JavascriptEngine extends Thread{
		
	private final ScriptEngine mScriptEngine;
	private final String 	   mEntryFunctionName;
	
	/**Sets up a Javascript engine, bound to the file specified by <b>jsFilePath</b>. When run is called on this thread,
	 * the function specified by <b>jsEntryFunction</b> will be called. 
	 * 
	 * @param jsFilePath
	 * @param jsEntryFunction
	 * @throws FileNotFoundException In case the .js file is not found in the specified path
	 * @throws ScriptException In case the .js file wasn't evaluated correctly
	 * @throws NoSuchMethodException  In the case the .js file doesn't have the method specified by jsEntryFunction
	 */
	public JavascriptEngine(File jsFile, String jsEntryFunction) throws FileNotFoundException, ScriptException, NoSuchMethodException {
		mEntryFunctionName = jsEntryFunction;
		
		//Set up the Nashorn script engine and then evaluate the script and whether the entry function exists
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		mScriptEngine = factory.getScriptEngine( new com.cluit.main.javascript.AllowedClassesFilter() );
		mScriptEngine.eval( new FileReader( jsFile ) );
		if (mScriptEngine.eval( 
				"if (typeof "+jsEntryFunction+" == 'function') "
						+ "{ var JS_ENGINE_TRUE = function() { return true; };  JS_ENGINE_TRUE();  } "
			  + "else "
			  			+ "{ var JS_ENGINE_FALSE= function() { return false; }; JS_ENGINE_FALSE(); } "
			).equals(false) ) {
			throw new NoSuchMethodException("No entry method! " + jsEntryFunction +" not found in script definition: "+ jsFile); 
		}
	}

	
	@Override
	public void run() {
		super.run();
				
		Invocable invocable = (Invocable) mScriptEngine;
		try {
			invocable.invokeFunction( mEntryFunctionName );
		} catch (NoSuchMethodException e) {		
			MethodMapper.invoke(Const.EXCEPTION_JS, "Javascript Exception occured! No such method: " + e.getMessage(), e);
		} catch (ScriptException e) {
			MethodMapper.invoke(Const.EXCEPTION_JS, "Javascript Exception occured! Script exception: " + e.getMessage(), e);
		} catch (Exception e) {
			MethodMapper.invoke(Const.EXCEPTION_JS, "Javascript Exception occured! Unknown exception: " + e.getMessage(), e);
		}
		
		MethodMapper.invoke(Const.METHOD_DONE_BUTTON_REACTIVATE);
	}
}
