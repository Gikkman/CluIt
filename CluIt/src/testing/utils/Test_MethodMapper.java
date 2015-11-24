package testing.utils;

import java.util.concurrent.atomic.AtomicReference;

import com.cluit.util.AoP.Invocation;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.methods.MiscUtils;

public class Test_MethodMapper {

	@SuppressWarnings("unchecked")
	public static void run(){
		System.out.println("Test - MethodMapper initialized");
		String a = "Fail";
		
		MethodMapper.addMethod("1", new Invocation() {
			@Override
			public void execute(Object... args) {
				((AtomicReference<String>)args[0]).set("Test");
			}
		});
		
		AtomicReference<String> ref = new  AtomicReference<String>();
		MethodMapper.invoke("1", ref );
		a = ref.get();
		checkInvocation(a, "Test");
		MethodMapper.removeMethod("1");
		if( MethodMapper.invoke("1") )
			System.out.println("Error! Removed invocation still active! " + MiscUtils.getStackPos());
		
		
		MethodMapper.addMethod("2", new Invocation() {
			@Override
			public void execute(Object... args) {
				((AtomicReference<String>)args[0]).set("Test2");
				MethodMapper.removeMethod("2");
			}
		});
		
		MethodMapper.invoke("2", ref );
		a = ref.get();
		checkInvocation(a, "Test2");
		checkInvocation( MethodMapper.invoke("2"), false);
		MethodMapper.removeMethod("2");
		
		System.out.println("Test - MethodMapper initialized");
	}
	
	private static void checkInvocation(String result, String facit){
		if( !result.equals(facit) )
			System.out.println("Error in Test_MethodMapper! Wrong invocation behaviour. Received: "+result+" Expected: " + facit + " " + MiscUtils.getStackPos());
	
	}
	
	private static void checkInvocation(boolean result, boolean facit){
		if( ! result == facit )
			System.out.println("Error in Test_MethodMapper! Wrong invocation behaviour. Received: "+result+" Expected: " + facit + " " + MiscUtils.getStackPos());
	
	}
}
