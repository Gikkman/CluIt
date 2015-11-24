package testing.utils;

import java.util.Date;
import java.util.Random;

import com.cluit.util.AoP.ReferencePasser;
import com.cluit.util.methods.MiscUtils;



@SuppressWarnings("deprecation")
public class Test_ReferencePasser {	
	private Test_ReferencePasser() {
	}
	
	public static void run(){
		System.out.println("Test - Reference Passer initialized");
		TestData d0 = new TestData(0, new Integer(0) );
		TestData d1 = new TestData(1, new Integer(1) );
		TestData d2 = new TestData(2, new Integer(2) );
		TestData d3 = new TestData(3, new Random() );
		TestData d4 = new TestData(3, new Date() );
		TestData result = null;
		
		ReferencePasser.storeReference("0", d0);
		result = ReferencePasser.getReference("0");		
		if( result != d0 )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d0 + " " + MiscUtils.getStackPos() );
		if( !result.equals(d0) )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d0 + " " + MiscUtils.getStackPos() );	
		if( (result = ReferencePasser.getReference("0")) != null ){
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + null + " " + MiscUtils.getStackPos() );
		} else {
			System.err.println(" -NOTE- This is expected as a result of the testing -NOTE-");
		}
		if( (result = ReferencePasser.getReference("1")) != null ){
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + null + " " + MiscUtils.getStackPos() );
		} else {
			System.err.println(" -NOTE- This is expected as a result of the testing -NOTE-");
		}
		
		ReferencePasser.storeReference("1", d1);
		ReferencePasser.storeReference("2", d2);
		result = ReferencePasser.getReference("1");
		if( result != d1 )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d1 + " " + MiscUtils.getStackPos() );
		if( !result.equals(d1) )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d1 + " " + MiscUtils.getStackPos() );	
		result = ReferencePasser.getReference("2");
		if( result != d2 )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d2 + " " + MiscUtils.getStackPos() );
		if( !result.equals(d2) )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d2 + " " + MiscUtils.getStackPos() );	
		
		ReferencePasser.storeReference("3", d3);
		ReferencePasser.storeReference("4", d4);
		result = ReferencePasser.getReference("3");
		if( result != d3 )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d3 + " " + MiscUtils.getStackPos() );
		if( !result.equals(d3) )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d3 + " " + MiscUtils.getStackPos() );
		result = ReferencePasser.getReference("4");
		if( result != d4 )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d4 + " " + MiscUtils.getStackPos() );
		if( !result.equals(d4) )
			System.out.println("Error! Wrong reference received! Recieved " + result +" Expected: " + d4 + " " + MiscUtils.getStackPos() );	
	
		System.out.println("Test - Reference Passer finalized");
	}
	
	private static class TestData{
		int i;
		Object o;
		TestData(int i, Object o){
			this.i = i;
			this.o = o;
		}
		
		boolean equals(TestData d){
			return i == d.i && o.equals(d.o) && o == d.o;
		}
	}	
}
