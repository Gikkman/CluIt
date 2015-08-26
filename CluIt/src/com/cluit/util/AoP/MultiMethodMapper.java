package com.cluit.util.AoP;

import java.util.HashMap;
import java.util.Map;

public class MultiMethodMapper {
	private static Map<String, Map<String, Invocation>> map = new HashMap<>();	
	private MultiMethodMapper() {}
		
		
	/**Adds a method to the method map, which can later be invoked. Each key can map to multiple methods, but each method's ID must be unique
	 * for a given key
	 * 
	 * @param key 
	 * @param id
	 * @param method 
	 * @return True if id was not listed for the given key
	 */
	public static synchronized boolean addMethod(String key, String id, Invocation method){		
		if( map.containsKey(key) ) {
			Map<String, Invocation> inner = map.get(key);
			if( inner.containsKey(id) ){
				return false;
			}
			else{
				inner.put(id, method);
				return true;
			}
		}
		else {
			Map<String, Invocation> inner = new HashMap<>();
			inner.put(id, method);
			map.put(key, inner);
			return true;
		}			
	}
	
	/**Removes the method with the given id for the given key
	 * 
	 * @param key
	 * @param id
	 * @return True if a method with the given id was found for the given key
	 */
	public static synchronized boolean removeMethod(String key, String id){
		if( map.containsKey(key) ){
			Map<String, Invocation> inner = map.get(key);
			if( inner.containsKey(id) ){
				inner.remove(id);
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	/**Invokes the methods mapped to 'key'.
	 * The user is responsible for casting and ordering the arguments correctly
	 * 
	 * @param key
	 * @param args
	 * @return True if the key was mapped to one or more method
	 */
	public static synchronized boolean invoke(String key, Object ... args){
		if(!map.containsKey(key)){		
			return false;
		}
		else {
			Map<String, Invocation> inner = map.get(key);
			if( inner.size() > 0){
				for( Invocation i :inner.values() )
					i.execute(args);
				return true;
			} 
			else {
				return false;
			}
		}
	}
}
