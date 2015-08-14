package com.cluit.util.AoP;

import java.util.HashMap;
import java.util.Map;

public class MethodMapper {
	private static Map<String, Invocation> map = new HashMap<>();	
	private MethodMapper() {}
		
		
	/**Adds a method to the method map, which can later be invoked. If the key is already in use, this method will instead return false
	 * and no mapping will be done.
	 * 
	 * @param key 
	 * @param method 
	 * @return True if key was not present in the map
	 */
	public static synchronized boolean addMethod(String key, Invocation method){

		if( map.containsKey(key) )
			return false;
		map.put(key, method);
		return true;
	}
	
	/**Removes a command and the corresponding key
	 * 
	 * @param key
	 * @return True if the key was present in the map
	 */
	public static synchronized boolean removeMethod(String key){
		
		if(!map.containsKey(key))
			return false;
		map.remove(key);
		return true;
	}
	
	/**Invokes the method mapped to 'key'. The object array can be used as output parameters as well as for passing arguments
	 * The user is responsible for casting and ordering the arguments correctly
	 * 
	 * @param key
	 * @param args
	 * @return True if the key was mapped to a method
	 */
	public static synchronized boolean invoke(String key, Object ... args){
		
		if(!map.containsKey(key))
			return false;
		
		map.get(key).execute(args);
		return true;
	}
	
}
