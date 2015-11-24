package testing.utils;

import com.cluit.util.AoP.MultiMethodMapper;
import com.cluit.util.methods.MiscUtils;

@SuppressWarnings("deprecation")
public class Test_MultiMethodMapper {
	private static String string = "";
	private static int	  value  = 0;
	
	public static void run(){
		System.out.println("Test - MultiMethodMapper initialized");
		boolean result;
		
		//Test just adding
		result = MultiMethodMapper.addMethod("A", "1", (args) -> { string = "A1"; } );
		checkInvocation(result, true);
		
		//Test adding with different key but same ID
		result = MultiMethodMapper.addMethod("B", "1", (args) -> { string = "B1"; } );
		checkInvocation(result, true);
		
		//Test adding with same key but different ID
		result = MultiMethodMapper.addMethod("A", "2", (args) -> { string = "A2"; } );
		checkInvocation(result, true);
		
		//Test adding with same key AND same ID
		result = MultiMethodMapper.addMethod("A", "2", (args) -> { string = "A2"; } );
		checkInvocation(result, false);
		
		//Test removing 
		result = MultiMethodMapper.removeMethod("B", "1");
		checkInvocation(result, true);
		
		//Test removing non-existant
		result = MultiMethodMapper.removeMethod("B", "1");
		checkInvocation(result, false);
		
		//Test re-adding
		result = MultiMethodMapper.addMethod("B", "1", (args) -> { string = "B1"; } );
		checkInvocation(result, true);
		
		//Test calling a single method
		MultiMethodMapper.invoke("B");
		checkString("B1");
		
		
		result = MultiMethodMapper.removeMethod("A", "1");
		checkInvocation(result, true);
		result = MultiMethodMapper.removeMethod("A", "2");
		checkInvocation(result, true);
		
		result = MultiMethodMapper.addMethod("A", "1", (args) -> value += 1 );
		checkInvocation(result, true);
		result = MultiMethodMapper.addMethod("A", "2", (args) -> value += 2 );
		checkInvocation(result, true);
		
		//Test invoking several methods
		value = 0;
		result = MultiMethodMapper.invoke("A");
		checkInvocation(result, true);
		checkInt(3);
		result =  MultiMethodMapper.invoke("A");
		checkInvocation(result, true);
		checkInt(6);		
		
		//Remove a method, and re-invoke
		result = MultiMethodMapper.removeMethod("A", "1");
		checkInvocation(result, true);
		result = MultiMethodMapper.invoke("A");
		checkInvocation(result, true);
		checkInt(8);
		
		//Remove the last method for a given key, and reinvoke
		result = MultiMethodMapper.removeMethod("A", "2");
		checkInvocation(result, true);
		result = MultiMethodMapper.invoke("A");
		checkInvocation(result, false);
		checkInt(8);
		
		System.out.println("Test - MultiMethodMapper finalized");
	}

	private static void checkInt(int facit) {
		if( value != facit )
			System.err.println("Error in Test_MethodMapper! Wrong invocation behaviour. Received: "+value+" Expected: " + facit + " " + MiscUtils.getStackPos());
	}
	private static void checkString(String facit) {
		if( ! string.matches(facit) )
			System.err.println("Error in Test_MethodMapper! Wrong invocation behaviour. Received: "+string+" Expected: " + facit + " " + MiscUtils.getStackPos());
	}
	private static void checkInvocation(boolean result, boolean facit){
		if( ! result == facit )
			System.err.println("Error in Test_MethodMapper! Wrong invocation behaviour. Received: "+result+" Expected: " + facit + " " + MiscUtils.getStackPos());
	}
}
