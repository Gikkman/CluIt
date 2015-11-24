package testing.clustering;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cluit.util.dataTypes.Entry;
import com.cluit.util.dataTypes.Space;
import com.cluit.util.methods.MiscUtils;

public class Test_Space {
	
	public static void run(){
		System.out.println("Test - Space initialized");
		
		testCreate();
		testAddCluster();
		testAddEntryToCluster();
		testGetNumbersOf_and_GetEntries();
		testGetEntryMembership();
		testIsClustered();
		testDistances();
		
		System.out.println("Test - Space finalized");
	}


	private static void testIsClustered() {
		Entry[] entries = { new Entry(0.0,0.0), new Entry(2.0,0.0), new Entry(-2.0,0.0), new Entry(0.0,5.0) };
		Space s = Space.create(2, entries);
		Space otherS = Space.create(2, entries);
		
		for(Entry e : entries){
			checkIfClustered(s, e, false);
		}
		
		s.addCluster(Integer.MAX_VALUE, Integer.MAX_VALUE);
		otherS.addCluster(Integer.MAX_VALUE, Integer.MAX_VALUE);
		
		s.addEntryToCluster( entries[0], 0);
		checkIfClustered(s, entries[0], true);
		checkIfClustered(otherS, entries[0], false);
		
		s.addCluster(5,5);
		s.addEntryToCluster(entries[0], 1);
		checkIfClustered(s, entries[0], true);
		checkIfClustered(otherS, entries[0], false);
	}


	private static void checkIfClustered(Space s, Entry e, boolean expected) {
		if( s.isClustered(e) != expected)
			System.err.println("Error in Test_Space! Wrong isClusteded status recieved! "+MiscUtils.getStackPos());
	}


	private static void testGetNumbersOf_and_GetEntries() {
		Space s;
		Entry[] entries0 = { };
		Entry[] entries1 = { new Entry(0.0,0.0)};
		Entry[] entries2 = { new Entry(0.0,0.0), new Entry(2.0,0.0) };
		Entry[] entries3 = { new Entry(0.0,0.0), new Entry(2.0,0.0), new Entry(-2.0,0.0) };
		Entry[] entries4 = { new Entry(0.0,0.0), new Entry(2.0,0.0), new Entry(-2.0,0.0), new Entry(0.0,5.0) };
		
		s = Space.create(2, entries0);
		if( s != null ){
			System.err.println("Space contract not maintained. No entries present "+MiscUtils.getStackPos());
		}
		s = Space.create(2, entries1);
		checkNumberOfEntries(s, 1);
		checkGetEntries(s, entries1);
		s = Space.create(2, entries2);
		checkNumberOfEntries(s, 2);
		checkGetEntries(s, entries2);
		s = Space.create(2, entries3);
		checkNumberOfEntries(s, 3);	
		checkGetEntries(s, entries3);
		s = Space.create(2, entries4);
		checkNumberOfEntries(s, 4);
		checkGetEntries(s, entries4);
		
		for( int i = 0; i < 100; i++){
			checkNumberOfClusters(s, i);
			s.addCluster(i,i);
		}
		checkNumberOfClusters(s, 100);
		
	}


	private static void checkGetEntries(Space s, Entry[] entries) {
		int nr = s.getNumberOfEntries();
		List<Entry> gotten = Arrays.asList( s.getAllEntries() );
		for( Entry e : entries ){
			nr -= gotten.contains(e) ? 1 : 0;
			gotten = gotten.stream().filter( obj -> obj != e ).collect( Collectors.toList() );			
		}
		if( nr != 0 )
			System.err.println("Error in Test_Space! Not all entries recieved! "+nr+" missed. " + MiscUtils.getStackPos());		
	}


	private static void checkNumberOfClusters(Space s, int i) {
		if( s.getNumberOfClusters() != i)
			System.err.println("Error int Test_Space. Wrong number of entries. Expected "+i+" Got "+s.getNumberOfClusters() +" "+MiscUtils.getStackPos() );
	}


	private static void checkNumberOfEntries(Space s, int i) {
		if( s.getNumberOfEntries() != i)
			System.err.println("Error int Test_Space. Wrong number of entries. Expected "+i+" Got "+s.getNumberOfEntries() +" "+MiscUtils.getStackPos() );
	}


	private static void testGetEntryMembership() {
		Entry[] entries = { new Entry(0.0,0.0), new Entry(2.0,0.0), new Entry(-2.0,0.0), new Entry(0.0,5.0) };
		Space s = Space.create(2, entries);
		
		s.addCluster(10,10);
		s.addCluster(-10,-10);
		
		checkEntryMembership(s, entries[0], -1);
		checkIfClustered(s, entries[0], false);
		s.addEntryToCluster( entries[0] , 0);
		checkEntryMembership(s, entries[0], 0);
		checkIfClustered(s, entries[0], true);
		s.addEntryToCluster(entries[0], 1);
		checkEntryMembership(s, entries[0], 1);
		checkIfClustered(s, entries[0], true);
	}


	private static void checkEntryMembership(Space s, Entry e, int expected) {
		if(s.getEntryMembership(e) != expected)
			System.err.println("Error in Test_Space! Wrong entry membership! " + MiscUtils.getStackPos());
	}


	private static void testAddEntryToCluster() {
		Entry[] entries = { new Entry(0.0,0.0), new Entry(2.0,0.0), new Entry(-2.0,0.0), new Entry(0.0,5.0) };
		Space s = Space.create(2, entries);
		
		s.addCluster(10,10);
		s.addCluster(-10,-10);
		
		checkIfClustered(s, entries[0], false);
		checkIfClustered(s, entries[1], false);
		checkIfClustered(s, entries[2], false);
		checkIfClustered(s, entries[3], false);
		
		checkAddEntryToCluster( s, entries[0], 1, 1);
		checkAddEntryToCluster( s, entries[1], 0, 0);
		checkAddEntryToCluster( s, entries[2], 1, 1);
		checkAddEntryToCluster( s, entries[3], 2, -1);
		
		checkIfClustered(s, entries[0], true);
		checkIfClustered(s, entries[1], true);
		checkIfClustered(s, entries[2], true);
		checkIfClustered(s, entries[3], false);
		
		checkAddEntryToCluster( s, entries[0], 2, -1);
		checkAddEntryToCluster( s, entries[1], 1, 1);
		checkAddEntryToCluster( s, entries[2], 1, 1);
		checkAddEntryToCluster( s, entries[3], 0, 0);
		
		checkIfClustered(s, entries[0], true);
		checkIfClustered(s, entries[1], true);
		checkIfClustered(s, entries[2], true);
		checkIfClustered(s, entries[3], true);
		
		checkEntryMembership(s, entries[0], 1);
		checkEntryMembership(s, entries[1], 1);
		checkEntryMembership(s, entries[2], 1);
		checkEntryMembership(s, entries[3], 0);
	}


	private static void checkAddEntryToCluster(Space s, Entry entry, int cluster, int expected) {
		int status = s.addEntryToCluster(entry, cluster);
		if( status != expected ){
			System.err.println("Error in Test_Space. Add Entry to Cluster failed. Expected "+expected+" Got "+status+" "+MiscUtils.getStackPos());
		}
	}


	private static void testAddCluster() {
		Entry[] entries = { new Entry(0,0), new Entry(2,0), new Entry(-2,0), new Entry(0,5) };
		Space s = Space.create(2, entries);
		
		checkAddCluster(s, 0, 1,1);
		checkAddCluster(s, -1, 1,1,1);
		checkAddCluster(s, -1, 1);
		checkAddCluster(s, -1);
		checkAddCluster(s, 1, -1,-1);
	}


	private static void checkAddCluster(Space s, int expected, double ... pos) {
		int status = s.addCluster(pos);
		if( status != expected )
			System.err.println("Error in Test_Space! Add cluster failed. Expected "+expected+" Got "+status+" "+MiscUtils.getStackPos());
	}


	private static void testDistances() {
		double[][] distanceFacit = {   {0, 1, 2, 1, Math.sqrt(2), Math.sqrt(5), 2,  Math.sqrt(5), Math.sqrt(8) },
									   {1, 0, 1, Math.sqrt(2), 1, Math.sqrt(2), Math.sqrt(5), 2,  Math.sqrt(5) },
									   {2, 1, 0, Math.sqrt(5), Math.sqrt(2), 1, Math.sqrt(8), Math.sqrt(5), 2  },
									   {1, Math.sqrt(2), Math.sqrt(5), 0, 1, 2, 1, Math.sqrt(2), Math.sqrt(5)  },
									   {Math.sqrt(2), 1, Math.sqrt(2), 1, 0, 1, Math.sqrt(2), 1, Math.sqrt(2)  },
									   {Math.sqrt(5), Math.sqrt(2), 1, 2, 1, 0, Math.sqrt(5), Math.sqrt(2), 1  },
									   {2, Math.sqrt(5), Math.sqrt(8), 1, Math.sqrt(2), Math.sqrt(5), 0, 1, 2  },
									   {Math.sqrt(5), 2, Math.sqrt(5), Math.sqrt(2), 1, Math.sqrt(2), 1, 0, 1  },
									   {Math.sqrt(8), Math.sqrt(5), 2, Math.sqrt(5), Math.sqrt(2), 1, 2, 1, 0  } };
		Entry[] entries = { new Entry(-1.0, 1.0), new Entry(0.0, 1.0), new Entry(1.0, 1.0),
							new Entry(-1.0, 0.0), new Entry(0.0, 0.0), new Entry(1.0, 0.0), 
							new Entry(-1.0,-1.0), new Entry(0.0,-1.0), new Entry(1.0,-1.0) };
		Space s = Space.create(2, entries);
		
		for(int i = 0; i < entries.length; i++){
			for( int j = 0; j < entries.length; j++){
				double dist = s.getDistance(entries[i], entries[j] );
				if( Math.abs( dist - distanceFacit[i][j] ) > 0.000001 )
					System.err.println("Error in Test_Space! Wrong distance at index ["+i+","+j+"] "+MiscUtils.getStackPos());
			}
		}
	}


	private static void testCreate() {
		Space s;
		Entry[] entries0D = { new Entry(),   new Entry(),     new Entry() };
		Entry[] entries1D = { new Entry(0.0),  new Entry(1.0),    new Entry(2.0) };
		Entry[] entries2D = { new Entry(0.0,0.0), new Entry(1.0,1.0), new Entry(2.0,2.0) };
		
		if( (s = Space.create(0, entries0D)) != null)
			System.err.println("Error in Test_Space. Constuctor behaviour error. "+s+" "+MiscUtils.getStackPos());
		if( (s = Space.create(1, entries1D)) == null)
			System.err.println("Error in Test_Space. Constuctor behaviour error. "+s+" "+MiscUtils.getStackPos());
		if( (s = Space.create(2, entries2D)) == null)
			System.err.println("Error in Test_Space. Constuctor behaviour error. "+s+" "+MiscUtils.getStackPos());
		
		Entry[] e = { entries0D[0], entries0D[0], entries0D[0], entries1D[0] };
		
		if( (s = Space.create(0, e)) != null)
			System.err.println("Error in Test_Space. Constuctor behaviour error. "+s+" "+MiscUtils.getStackPos());
		if( (s = Space.create(1, e)) == null)
			System.err.println("Error in Test_Space. Constuctor behaviour error. "+s+" "+MiscUtils.getStackPos());
		checkNumberOfEntries(s, 1);
	}
}