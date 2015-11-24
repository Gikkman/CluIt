package testing._helpers;

import java.util.Random;

public class FakeRandom extends Random {
	private static final long serialVersionUID = 5769247147614474920L;
	
	private final int[] numbers;
	private int idx = 0;
	
	public FakeRandom(int...i){
		numbers = i;
		
		if( numbers.length == 0 )
			System.err.println("Error! Initialization of TestRandom failed! Constructor argument " + i + " is invalid");
	}
	
	public int nextInt(){		
		int i = numbers[idx];
		idx = (idx+1) % numbers.length;
		return i;
	}
	
	public int nextInt(int ceil){
		if( ceil == 0)
			return 0;
		
		return nextInt() % ceil;
	}
}
