package com.cluit.util.AoP;

import java.util.concurrent.atomic.AtomicReference;

/**<b>This class is currently not used.</b> <br><br>
 * 
 * Used to pass variables between different parts of the program without coupling sender and receiver. Also avoids the Singleton problem, in that
 * a reference can only be extracted once.
 * 
 * @author Simon
 *
 */
@Deprecated
public class ReferencePasser {

	/**Adds a reference to the reference map. A reference can be extracted ONLY ONCE. Each reference needs a UNIQUE key.
	 * 
	 * This method is smart. Simply pass the object you want to store a reference to as the value-parameter.
	 * 
	 * @param key
	 * @param value The object for which a reference should be stored
	 * @return True if the reference was successful stored (False if the key conflicted)
	 */
	public static <T> void storeReference (String key, T value){
		
		MethodMapper.addMethod(key, new ReferenceWrapper<T>(value) );	
		
	}
	
	/**Retrieves a reference from the reference map. A reference can be extracted only one, and is then removed.
	 * This method is smart. Simply assign the returned object directly to a variable.
	 * <br><br>
	 * Example: Object o = ReferencePasser.getReferenec("key");<br>
	 * Now, modifications to 'o' will directly affect the original object as well
	 * 
	 * @param key
	 * @return The reference (or NULL, if no reference was mapped to the key)
	 */
	public static <T> T getReference (String key){
		AtomicReference<T> ref = new AtomicReference<T>();
		if (MethodMapper.invoke(key, key, ref) )
			return ( ref.get() );
		else{
			System.err.println("The requested key \"" + key + "\" did not map to a reference. " + com.cluit.util.methods.MiscUtils.getStackPos() );
			return null;
		}
	}
	
	private static class ReferenceWrapper<E> implements Invocation {
		
		private AtomicReference<E> ref;
		ReferenceWrapper(E e){
			ref = new AtomicReference<E>(e);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void execute(Object... args) {
			((AtomicReference<E>) args[1]).set( ref.get() );
			MethodMapper.removeMethod( (String) args[0] );
		}		
	}
}
