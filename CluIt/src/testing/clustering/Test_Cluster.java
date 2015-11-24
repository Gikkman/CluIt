package testing.clustering;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.cluit.util.dataTypes.Cluster;
import com.cluit.util.dataTypes.Entry;
import com.cluit.util.methods.MiscUtils;


@SuppressWarnings("unused")
public class Test_Cluster {
	public static void run(){
		System.out.println("Test - Cluster initialized");
		
		testConstructor();	
		testAddRemoveEntry();	
		testGetNumberOfMembers();
		testGetMembers();
		testCalculateCentoid();		
		testFurthestMemberDistance();
		testSquareError();
		
		System.out.println("Test - Cluster finalized");
	}	
	
	private static void testGetMembers() {
		double[] centoid2D = { 0, 0 };
		Cluster c2D = new Cluster( centoid2D );	
		Entry centoid = new Entry(centoid2D);
		Entry[] entries2D = { new Entry(1.0,1.0), new Entry(2.0,2.0), new Entry(5.0,5.0)  };
		
		checkMembers(c2D);
		
		c2D.add(centoid);
		checkMembers(c2D, centoid);
		
		c2D.add(entries2D[0]);
		c2D.add(entries2D[1]);
		c2D.add(entries2D[2]);
		checkMembers(c2D, centoid, entries2D[0], entries2D[1], entries2D[2]);		
		
		c2D.removeEntry(entries2D[1]);
		c2D.removeEntry(centoid);
		checkMembers(c2D, entries2D[0], entries2D[2]);
	}


	


	private static void testAddRemoveEntry() {
		double[] centoid2D = { 0.0, 0.0 };
		Cluster c2D = new Cluster( centoid2D );	
		Entry centoid = new Entry(centoid2D);
		Entry[] entries2D = { new Entry(1.0,1.0), new Entry(2.0,2.0), new Entry(5.0,5.0)  };
		
		checkIfMember(c2D, centoid, false );
		if( c2D.add(centoid) == false )
			System.err.println("Error! Wrong add report! " + MiscUtils.getStackPos() );
		checkIfMember(c2D, centoid, true );
		if( c2D.add(centoid) == true )
			System.err.println("Error! Wrong removal report! " + MiscUtils.getStackPos() );
		
		checkIfMember(c2D, entries2D[0], false );
		if( c2D.add( entries2D[0]) == false )
			System.err.println("Error! Wrong add report! " + MiscUtils.getStackPos() );
		checkIfMember(c2D, entries2D[0], true );
		if( !c2D.removeEntry( entries2D[0] ) )
			System.err.println("Error! Wrong removal report! " + MiscUtils.getStackPos() );
		checkIfMember(c2D, entries2D[0], false );	
		if( c2D.removeEntry( entries2D[0] ) )
			System.err.println("Error! Wrong removal report! " + MiscUtils.getStackPos() );
	}

	

	private static void testCalculateCentoid() {
		double[] centoid2D = { 0.0, 0.0 };
		Cluster c2D = new Cluster( centoid2D );	
		Entry[] entries2D = { new Entry(1.0,1.0), new Entry(2.0,2.0), new Entry(5.0,5.0)  };
		
		checkCentoidPosition(c2D, 0,0 );
		c2D.add( new Entry(centoid2D) );
		checkCentoidPosition(c2D, 0,0 );
		c2D.calculateCentoid();
		checkCentoidPosition(c2D, 0,0 );
		
		c2D.add( new Entry( entries2D[0]) );
		checkCentoidPosition(c2D, 0,0 );
		c2D.calculateCentoid();
		checkCentoidPosition(c2D, 0.5, 0.5 );
		
		c2D.add( new Entry( entries2D[1]) );
		checkCentoidPosition(c2D, 0.5, 0.5 );
		c2D.calculateCentoid();
		checkCentoidPosition(c2D, 1,1 );
		
		c2D.add( new Entry( entries2D[2]) );
		checkCentoidPosition(c2D, 1, 1 );
		c2D.calculateCentoid();
		checkCentoidPosition(c2D, 2,2 );
	}

	private static void testConstructor(){
		double[] centoid = { 0, 0 };
		Cluster c = new Cluster( centoid );
		if( c == null)
			System.err.println("Error in Test_Cluster! Constructor failed! " + MiscUtils.getStackPos() );
	}
	
	private static void testGetNumberOfMembers(){
		double[] centoid = { 0, 0 };
		Cluster c = new Cluster( centoid );
		
		Entry[] entries = { new Entry(1.0,1.0), new Entry(5.0,5.0), new Entry(8.0,1.0) };
		
		checkNumberOfMembers(c, 0);
		c.add(entries[0]);
		checkNumberOfMembers(c, 1);
		c.add(entries[1]);
		checkNumberOfMembers(c, 2);
		c.add(entries[2]);
		checkNumberOfMembers(c, 3);
		
		c.removeEntry(entries[1] );
		checkNumberOfMembers(c, 2);
		c.removeEntry(entries[2] );
		checkNumberOfMembers(c, 1);
		c.removeEntry(entries[0] );
		checkNumberOfMembers(c, 0);
		
	}

	private static void testFurthestMemberDistance(){
		double[] centoid2D = { 0, 0 };
		Cluster c2D = new Cluster( centoid2D );	
		Entry[] entries2D = { new Entry(1.0,1.0), new Entry(0.0,-5.0), new Entry(8.0,0.0), new Entry(0.0,2.0)  };
		
		//Add and remove
		checkFurthestMemberDistance(c2D, -1);	//No members	
		c2D.add( entries2D[0] );
		checkFurthestMemberDistance(c2D, Math.sqrt(2) );
		c2D.add( entries2D[1] );
		checkFurthestMemberDistance(c2D, 5 );
		c2D.add( entries2D[2] );
		checkFurthestMemberDistance(c2D, 8 );
		c2D.removeEntry( entries2D[2] );
		checkFurthestMemberDistance(c2D, 5 );
		c2D.removeEntry( entries2D[0] );
		checkFurthestMemberDistance(c2D, 5 );
		c2D.removeEntry( entries2D[1] );
		checkFurthestMemberDistance(c2D, -1 );
		
		//Move centoid
		c2D = new Cluster( centoid2D );	
		c2D.add( new Entry(centoid2D));
		checkFurthestMemberDistance(c2D, 0);
		c2D.add( entries2D[3] );
		c2D.calculateCentoid();
		//Centoid should now be at (0,1)
		c2D.add( entries2D[0] );
		checkFurthestMemberDistance(c2D, 1 );
		c2D.add( entries2D[1] );
		checkFurthestMemberDistance(c2D, 6 );
		
		//Test in 3D as well
		double[] centoid3D = { 1, 1, 1 };
		Cluster c3D = new Cluster( centoid3D );	
		Entry[] entries3D = { new Entry(1.0,2.0,2.0 ), new Entry(1.0,1.0,-5.0), new Entry(8.0,1.0,1.0)  };
		checkFurthestMemberDistance(c3D, -1);	
		c3D.add( entries3D[0] );
		checkFurthestMemberDistance(c3D, Math.sqrt(2) );
		c3D.add( entries3D[1] );
		checkFurthestMemberDistance(c3D, 6 );
		c3D.add( entries3D[2] );
		checkFurthestMemberDistance(c3D, 7 );
	}
	
	private static void testSquareError() {
		double[] centoid1d = { 0 };
		Cluster c1D = new Cluster(centoid1d);
		Entry[] entries1D = { new Entry(1.0), new Entry(-2.0), new Entry(5.0), new Entry(-10.0)  };	
		checkSquaredError(c1D, 0);
		
		for(Entry e : entries1D)
			c1D.add(e);
		checkSquaredError(c1D, 130);
		
		
		double[] centoid2D = { 0, 0 };
		Cluster c2D = new Cluster( centoid2D );	
		Entry[] entries2D = { new Entry(1.0,1.0), new Entry(-2.0,-2.0), new Entry(3.0,0.0), new Entry(0.0,3.0)  };	
		for(Entry e: entries2D)
			c2D.add(e);
		checkSquaredError(c2D, 2+8+9+9);
		
	}

	private static void checkSquaredError(Cluster c, int facit) {
		double error = c.getSquaredError();
		if( Math.abs(error - facit) > 0.0001 ){
			System.err.println("Error in Test_Cluster! Wrong sum of squared errors! Expected " + facit +" Got " + c.getSquaredError() +" "+ MiscUtils.getStackPos());
		}
		
	}

	private static void checkNumberOfMembers(Cluster c, int i) {
		if( c.getNumberOfMembers() != i)
			System.err.println("Error in Test_Cluster! Wrong number of members! Expected "+i+" Got "+c.getNumberOfMembers()+" "+MiscUtils.getStackPos());
	}
	
	private static void checkFurthestMemberDistance(Cluster c, double d) {
		if( Math.abs( c.getFurthestMembersDistance() - d ) > 0.00001 )
			System.err.println("Error in Test_Cluster! Wrong furthest distance! Expected "+d+" Got "+c.getFurthestMembersDistance()+" "+MiscUtils.getStackPos());
	
	}

	private static void checkCentoidPosition(Cluster c, double ... pos) {
		Entry centoid = c.getCentoid();
		if( centoid.getDimensions() != pos.length ){
			System.err.println("Error in Test_Cluster! Dimensions missmatch! Centoid.dim " + centoid.getDimensions() +" Expected "+pos.length +" " +MiscUtils.getStackPos());
			return;
		}
		double[] centPos = centoid.getCoordinates();
		for( int i = 0; i < pos.length; i++){
			if( Math.abs( centPos[i] - pos[i] ) > 0.000001 )
				System.err.println("Error in Test_Cluster! Position missmatch! Centoid[i] " + centPos[i] +" Expected "+pos[i] +" " +MiscUtils.getStackPos());
		}
	}
	
	private static void checkIfMember(Cluster c, Entry e, boolean expected) {
		if( c.isMember(e) != expected )
			System.err.println("Error in Test_Cluster! Incorrect membership reported! " + MiscUtils.getStackPos() );
	}
	
	private static void checkMembers(Cluster c2d, Entry ... members) {
		if( c2d.getNumberOfMembers() != members.length ){
			System.err.println("Error in Test_Cluster! Wrong number of members! Expected "+members.length+" Got "+c2d.getNumberOfMembers()+" "+MiscUtils.getStackPos());
			return;
		}
		int nr = c2d.getNumberOfMembers();
		List<Entry> gotten = Arrays.asList( c2d.getMembers() );
		for( Entry e : members ){
			nr -= gotten.contains(e) ? 1 : 0;
			gotten = gotten.stream().filter( obj -> obj != e ).collect( Collectors.toList() );			
		}
		if( nr != 0 )
			System.err.println("Error in Test_Cluster! Not all members recieved! "+nr+" missed. " + MiscUtils.getStackPos());
		
	}
}
