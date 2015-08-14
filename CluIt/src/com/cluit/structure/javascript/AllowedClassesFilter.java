package com.cluit.structure.javascript;

import jdk.nashorn.api.scripting.ClassFilter;

public class AllowedClassesFilter implements ClassFilter{

	@Override
	public boolean exposeToScripts(String s) {
		//TODO: Setup a proper exposure filter
		return true;
	}
	

}
