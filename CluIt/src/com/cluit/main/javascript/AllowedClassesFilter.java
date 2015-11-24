package com.cluit.main.javascript;

import jdk.nashorn.api.scripting.ClassFilter;

/**An Exposure filter can tell the script engine which java classes the script engine should be allowed to see and which should be left hidden.
 * 
 * Currently, all java classes are exposed to the engine.
 * 
 * @author Simon
 *
 */
public class AllowedClassesFilter implements ClassFilter{

	/**
	 * Should the Java class of the specified name be exposed to scripts?
	 */
	@Override
	public boolean exposeToScripts(String s) {
		return true;
	}
	

}
