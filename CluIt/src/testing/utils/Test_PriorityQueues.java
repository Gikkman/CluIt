package testing.utils;

import java.util.ArrayList;

import com.cluit.util.structures.KeyPriorityQueue;
import com.cluit.util.structures.KeyPriorityQueue_Max;
import com.cluit.util.structures.KeyPriorityQueue_Min;

public class Test_PriorityQueues {

	public static void run(String[] args) {
		testMinQueue();
		testMaxQueue();
	}
	
	private static int[] minFacit = {5, 1, 2, 3, 4};
	private static int[] maxFacit = {1, 3, 2, 4, 5};
	
	private static void testMinQueue() {
		System.out.println("Test - MinQueue initialized");		
		KeyPriorityQueue<Integer> q = new KeyPriorityQueue_Min<Integer>();
		q.add(1,   1);
		q.add(1.1, 2);
		q.add(1.1, 3);
		q.add(2,   4);
		q.add(0.5, 5);
		
		ArrayList<Double > arrD = new ArrayList<Double>(); for( double d : q.getKeys() ) arrD.add(d);
		ArrayList<Integer> arrI = q.values();
		
		for( int i = 1; i < 5; i++)
			if( !arrI.contains(i) )
				System.err.println("Error in MinQueue.getValues! Couldn't find " + i);

		if( ! (arrD.remove(0.5) && arrD.remove(1.0) && arrD.remove(2.0) && arrD.remove(1.1) && arrD.remove(1.1) ) )
			System.err.println("Error in MinQueue.getKeys! An element was missing");
			
		//Test peak/pop
		for( int i = 0; i < 5; i++){
			int j = q.peek();
			if( q.peek() != minFacit[i] )
				System.err.println("Error in MinQueue! Expected " + minFacit[i] +" but found " + q.peek() );
			int p = q.poll();
			if( p != j )
				System.err.println("Error in Queue! Peek-value != Poll-value  ..  Peek: " + j  +" Poll: " + p);
			
		}	
		System.out.println("Test - MinQueue finalized");
	}

	private static void testMaxQueue() {
		System.out.println("Test - MaxQueue initialized");
		KeyPriorityQueue<Integer> q = new KeyPriorityQueue_Max<Integer>();
		q.add(2,   1);
		q.add(1.1, 3);
		q.add(1.1, 2);
		q.add(1,   4);
		q.add(0.5, 5);
		
		for( int i = 0; i < 5; i++){
			int j = q.peek();
			if( q.peek() != maxFacit[i] )
				System.err.println("Error in MaxQueue! Expected " + maxFacit[i] +" but found " + q.peek() );
			int p = q.poll();
			if( p != j )
				System.err.println("Error in Queue! Peek-value != Poll-value  ..  Peek: " + j  +" Poll: " + p);
			
		}	
		System.out.println("Test - MaxQueue finalized");
	}
	
	

}
