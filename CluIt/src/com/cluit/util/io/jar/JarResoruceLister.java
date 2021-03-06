package com.cluit.util.io.jar;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**Static class which only contains a single method. Used for listing files inside a .jar file
 * 
 * @author Simon
 *
 */
public class JarResoruceLister {

	  /**Thanks to Greg Briggs for the following code. Gikkman made some slight modifications
	   * 
	   * List directory contents for a resource folder. Not recursive.
	   * This is basically a brute-force implementation.
	   * Works for regular files and also JARs.
	   * 
	   * @author Greg Briggs
	   *
	   * @param clazz Any java class within the jar.
	   * @param path Path to the directory you want to list items in. The path should be relative to clazz. Should end with "/", but not start with one.
	   * @return Just the name of each member item, not the full paths.
	   * @throws URISyntaxException 
	   * @throws IOException 
	   */
	@SuppressWarnings("rawtypes")
	public static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
	      URL dirURL = clazz.getResource(path);
	      if (dirURL != null && dirURL.getProtocol().equals("file")) {
	        /* A file path: easy enough */
	        return new File(dirURL.toURI()).list();
	      } 

	      
        /* 
         * In case of a jar file, we can't actually find a directory.
         * Have to assume the same jar as clazz.
         */
        String me = clazz.getName().replace(".", "/")+".class";
        dirURL = clazz.getClassLoader().getResource(me);
	      
	      
	      if (dirURL.getProtocol().equals("jar")) {
	        /* A JAR path */
	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
	        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
	        
	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar  
	        Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
	       
	        while(entries.hasMoreElements()) {
	          String name = entries.nextElement().getName();
	          if (name.contains(path)) { //filter according to the path
	        	  int dirCount = name.lastIndexOf("/") + 1; //get the location directly after the last directory 
	        	  String entry = name.substring(dirCount, name.length() );
	        	  
	        	  if( entry.length() > 0 ) //Skip directories
	        		  result.add(entry);
	          }
	        }
	        
	        jar.close();
	        return result.toArray(new String[result.size()]);
	      } 
	        
	      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
	  }
}
