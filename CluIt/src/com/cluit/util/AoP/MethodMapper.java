package com.cluit.util.AoP;

import java.util.HashMap;
import java.util.Map;

/**This class uses Aspect-oriented programming to allow classes to register (and call) methods in other parts of the program without
 * knowledge of the containing class. <br>
 * By registering an invocation with a given key, other classes can later on call the invocation only by having the key.
 * 
 * @author Simon
 *
 */
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
	public static synchronized void addMethod(String key, Invocation method){
		map.put(key, method);
	}
	
	/**Removes a command and the corresponding key
	 * 
	 * @param key
	 * @return True if the key was present in the map
	 */
	public static synchronized boolean removeMethod(String key){
		if(!map.containsKey(key)){
			return false;	
		}
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
		if(!map.containsKey(key)){		
			return false;
		}
		map.get(key).execute(args);
		return true;
	}
	
}
