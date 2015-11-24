package testing.clustering;
/*package clustering;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import clustering.dataTypes.Point;
import clustering.structures.PointCluster;
import clustering.structures.PointSpace;

public class OLD_Test_PointSpace {
	
	public static void run(){
		System.out.println("Test - PointSpace initialized");
		testConstructor();
		testAddPoint();
		testAddCluster();
		testNumberOfPoints();
		testNumberOfClusters();
		testGetPoint();
		testGetCluster();
		testGetPointMembership();
		System.out.println("Test - PointSpace finalized");
	}

	@SuppressWarnings("unused")
	private static void testConstructor() {
		PointSpace ps = PointSpace.create(0);		
	
	}
	
	private static void testAddPoint() {
		PointSpace ps = PointSpace.create(0);
		checkAddPoint( ps, new Point(0,0) 	 , 0);
		checkAddPoint( ps, new Point() 	  	 , 1);
		checkAddPoint( ps, new Point(1,2,3,4), 2 );
		
		Point[] pointsArray = { new Point(0,1), new Point(1,0),
								new Point(1,1), new Point(0,0) 
		};
		
		checkAddPointsArray(ps, pointsArray );
		Random rnd = new Random();
		
		List<Point> pointLinkedList = new LinkedList<Point>();
		for( int i = 0; i < 4; i++ ){
			pointLinkedList.add( new Point (rnd.nextInt(10), rnd.nextInt(10) ) );
		}
		checkAddPointsCollection(ps, pointLinkedList);
		
		List<Point> pointArrayList = new ArrayList<Point>();
		for( int i = 0; i < 4; i++ ){
			pointArrayList.add( new Point (rnd.nextInt(10), rnd.nextInt(10) ) );
		}
		checkAddPointsCollection(ps, pointArrayList);
		
		Collection<Point> pointStack = new Stack<Point>();
		for( int i = 0; i < 4; i++ ){
			pointStack.add( new Point (rnd.nextInt(10), rnd.nextInt(10) ) );
		}
		checkAddPointsCollection(ps, pointStack);
		
		Collection<Point> pointArrayDeque = new ArrayDeque<Point>();
		for( int i = 0; i < 4; i++ ){
			pointArrayDeque.add( new Point (rnd.nextInt(10), rnd.nextInt(10) ) );
		}
		checkAddPointsCollection(ps, pointArrayDeque);
		
	}
	
	private static void checkAddPointsCollection(PointSpace ps, Collection<Point> pointCollection) {
		if( !ps.addPoints(pointCollection) )
			System.err.println("Error in Test_PointSpace! Couldn't add pointsCollection! " + new Exception().getStackTrace()[1]);	
	}

	private static void checkAddPointsArray(PointSpace ps, Point[] pointsArray) {
		if( !ps.addPoints(pointsArray) )
			System.err.println("Error in Test_PointSpace! Couldn't add pointsArray! " + new Exception().getStackTrace()[1]);
	}

	private static void checkAddPoint(PointSpace ps, Point point, int i) {
		int id = ps.addPoint( point );
		if( id != i )
			System.err.println("Error in Test_PointSpace! Wrong ID received! Got "+id+" expected "+i+" " + new Exception().getStackTrace()[1]);
	}

	private static void testAddCluster() {
		PointSpace ps = PointSpace.create(0);
		Point p1 = new Point(0,0);
		Point p2 = new Point(2,2);
		Point p3 = new Point(3,4);
		Point p4 = new Point( Integer.MIN_VALUE, Integer.MIN_VALUE);
		
		checkAddCluster(ps, new PointCluster(p1, false), 0);
		checkAddCluster(ps, new PointCluster(p2, true),  1);
		checkAddCluster(ps, new PointCluster(p1, false), 2);
		checkAddCluster(ps, new PointCluster(p4, true),  3);
		checkAddCluster(ps, new PointCluster(p4, true),  4);
		
		PointCluster[] pointsArray = { new PointCluster( p1, true ), new PointCluster(p1, true),
									   new PointCluster( p2, false), new PointCluster(p3, true) 
		};

		checkAddClusterArray(ps, pointsArray );
		Random rnd = new Random();
		
		List<PointCluster> pointLinkedList = new LinkedList<PointCluster>();
		for( int i = 0; i < 4; i++ ){
		pointLinkedList.add( new PointCluster(new Point(rnd.nextInt(10), rnd.nextInt(10)), true ) );
		}
		checkAddClusterCollection(ps, pointLinkedList);
		
		List<PointCluster> pointArrayList = new ArrayList<PointCluster>();
		for( int i = 0; i < 4; i++ ){
		pointArrayList.add( new PointCluster ( new Point(rnd.nextInt(10), rnd.nextInt(10)), true ) );
		}
		checkAddClusterCollection(ps, pointArrayList);
		
		Collection<PointCluster> pointStack = new Stack<PointCluster>();
		for( int i = 0; i < 4; i++ ){
		pointStack.add( new PointCluster ( new Point(rnd.nextInt(10), rnd.nextInt(10)), true ) );
		}
		checkAddClusterCollection(ps, pointStack);
		
		Collection<PointCluster> pointArrayDeque = new ArrayDeque<PointCluster>();
		for( int i = 0; i < 4; i++ ){
		pointArrayDeque.add( new PointCluster ( new Point(rnd.nextInt(10), rnd.nextInt(10)), true ) );
		}
		checkAddClusterCollection(ps, pointArrayDeque);
	}

	private static void checkAddClusterCollection(PointSpace ps, Collection<PointCluster> pointCollection) {
		if( !ps.addClusters(pointCollection) )
			System.err.println("Error in Test_PointSpace! Couldn't add pointsClusterCollection! " + new Exception().getStackTrace()[1]);	
	}

	private static void checkAddClusterArray(PointSpace ps, PointCluster[] pointsClusterArray) {
		if( !ps.addClusters(pointsClusterArray) )
			System.err.println("Error in Test_PointSpace! Couldn't add pointsClusterArray! " + new Exception().getStackTrace()[1]);	
	}

	private static void checkAddCluster(PointSpace ps, PointCluster pointCluster, int i) {
		int id = ps.addCluster( pointCluster );
		if( id != i )
			System.err.println("Error in Test_PointSpace! Wrong ID received! Got "+id+" expected "+i+" " + new Exception().getStackTrace()[1]);
	}

	@SuppressWarnings("deprecation")
	private static void testNumberOfPoints() {
		PointSpace ps = PointSpace.create(0);
		Point[] pts = { new Point(0,0),  new Point(1,1),  new Point(0,0),  new Point(2,10) };
		
		for( int i = 0; i < pts.length; i++){
			ps.addPoint( pts[i] );
			if( ps.getNumberOfPoints() != i+1 )
				System.err.println("Error in Test_PointSpace! Wrong number of points! Got "+ps.getNumberOfPoints()+" expected "+(i+1)+" " + new Exception().getStackTrace()[0]);
		}
		
		ps.clearSpace();
		if( ps.getNumberOfPoints() != 0 )
			System.err.println("Error in Test_PointSpace! Wrong number of points! Got "+ps.getNumberOfPoints()+" expected "+0+" " + new Exception().getStackTrace()[0]);
	
		ps.addPoints(pts);
		if( ps.getNumberOfPoints() != 4 )
			System.err.println("Error in Test_PointSpace! Wrong number of points! Got "+ps.getNumberOfPoints()+" expected "+4+" " + new Exception().getStackTrace()[0]);
	
		//This one's a bit tricky, but since the same objects are added to the space,
		//and the contract says that each object has to be unique, the number
		//of members shouldn't change as a result of this operation.
		Collection<Point> pCol = Arrays.asList(pts);
		ps.addPoints(pCol);
		if( ps.getNumberOfPoints() != 4 )
			System.err.println("Error in Test_PointSpace! Wrong number of points! Got "+ps.getNumberOfPoints()+" expected "+4+" " + new Exception().getStackTrace()[0]);
	
		//Now, since they are new object, they should be added as usual
		Point[] pts2 = { new Point(0,0),  new Point(1,1),  new Point(0,0),  new Point(2,10) };
		Collection<Point> pCol2 = Arrays.asList(pts2);
		ps.addPoints(pCol2);
		if( ps.getNumberOfPoints() != 8 )
			System.err.println("Error in Test_PointSpace! Wrong number of points! Got "+ps.getNumberOfPoints()+" expected "+8+" " + new Exception().getStackTrace()[0]);
	
	}

	@SuppressWarnings("deprecation")
	private static void testNumberOfClusters() {
		PointSpace ps = PointSpace.create(0);
		Point[] pts = { new Point(0,0),  new Point(1,1),  new Point(0,0),  new Point(2,10) };
		PointCluster[] clu = { new PointCluster( pts[0], true), new PointCluster(pts[1], true),
						  	   new PointCluster( pts[2], false), new PointCluster(pts[3], false) 
		};
		
		for( int i = 0; i < pts.length; i++){
			ps.addCluster( clu[i] );
			if( ps.getNumberOfClusters() != i+1 )
				System.err.println("Error in Test_PointSpace! Wrong number of clusters! Got "+ps.getNumberOfClusters()+" expected "+(i+1)+" " + new Exception().getStackTrace()[0]);
		}
		
		ps.clearSpace();
		if( ps.getNumberOfClusters() != 0 )
			System.err.println("Error in Test_PointSpace! Wrong number of clusters! Got "+ps.getNumberOfClusters()+" expected "+0+" " + new Exception().getStackTrace()[0]);
	
		ps.addClusters(clu);
		if( ps.getNumberOfClusters() != 4 )
			System.err.println("Error in Test_PointSpace! Wrong number of clusters! Got "+ps.getNumberOfClusters()+" expected "+4+" " + new Exception().getStackTrace()[0]);
	
		//This one's a bit tricky, but since the same objects are added to the space,
		//and the contract says that each object has to be unique, the number
		//of members shouldn't change as a result of this operation.
		Collection<PointCluster> pCol = Arrays.asList(clu);
		ps.addClusters(pCol);
		if( ps.getNumberOfClusters() != 4 )
			System.err.println("Error in Test_PointSpace! Wrong number of clusters! Got "+ps.getNumberOfClusters()+" expected "+4+" " + new Exception().getStackTrace()[0]);
	
		//Now, since they are new object, they should be added as usual
		PointCluster[] clu2 = { new PointCluster( pts[0], true), new PointCluster(pts[1], true),
			  	   new PointCluster( pts[2], false), new PointCluster(pts[3], false) 
		};
		Collection<PointCluster> pCol2 = Arrays.asList(clu2);
		ps.addClusters(pCol2);
		if( ps.getNumberOfClusters() != 8 )
			System.err.println("Error in Test_PointSpace! Wrong number of clusters! Got "+ps.getNumberOfClusters()+" expected "+8+" " + new Exception().getStackTrace()[0]);
	
	}

	private static void testGetPoint() {
		PointSpace ps = PointSpace.create(0);
		Point[] pts = { new Point(0,0),  new Point(1,1),  new Point(0,0),  new Point(2,10) };
		
		for( int i = 0; i < pts.length; i++){
			ps.addPoint( pts[i] );
		}
		
		checkIfCorrectPoint(ps, pts[0], 0);	
		checkIfCorrectPoint(ps, pts[1], 1);	
		checkIfCorrectPoint(ps, pts[2], 2);	
		checkIfCorrectPoint(ps, pts[3], 3);	
		
	}

	private static void checkIfCorrectPoint(PointSpace ps, Point point, int i) {
		int id = ps.getPointID(point);
		if( id != i )
			System.err.println("Error in Test_PointSpace! Wrong point data received. Got ID: " + id +" expected: "+ i + " " + new Exception().getStackTrace()[0] );
		
		Point p = ps.getPoint(i);
		if( ! (p == point) )
			System.err.println("Error in Test_PointSpace! Wrong point received. Got ID: " + p +" expected: "+ point + " " + new Exception().getStackTrace()[0] );
	}

	private static void testGetCluster() {
		PointSpace ps = PointSpace.create(0);
		Point[] pts = { new Point(0,0),  new Point(1,1),  new Point(0,0),  new Point(2,10) };
		PointCluster[] clu = { new PointCluster( pts[0], true), new PointCluster(pts[1], true),
			  	   new PointCluster( pts[2], false), new PointCluster(pts[3], false) 
		};
		
		for( int i = 0; i < pts.length; i++){
			ps.addCluster( clu[i] );
		}
		
		checkIfCorrectCluster(ps, clu[0], 0);	
		checkIfCorrectCluster(ps, clu[1], 1);	
		checkIfCorrectCluster(ps, clu[2], 2);	
		checkIfCorrectCluster(ps, clu[3], 3);	
		
	}

	private static void checkIfCorrectCluster(PointSpace ps, PointCluster pointCluster, int i) {
		int id = ps.getClusterID(pointCluster);
		if( id != i )
			System.err.println("Error in Test_PointSpace! Wrong cluster data received. Got ID: " + id +" expected: "+ i + " " + new Exception().getStackTrace()[0] );
		
		PointCluster p = ps.getCluster(i);
		if( ! (p == pointCluster) )
			System.err.println("Error in Test_PointSpace! Wrong cluster received. Got ID: " + p +" expected: "+ pointCluster + " " + new Exception().getStackTrace()[0] );
	
	}

	private static void testGetPointMembership() {
		PointSpace ps = PointSpace.create(0);
		Point[] pts = { new Point(0,0),  new Point(1,1),  
						new Point(1,0),  new Point(2,10),
						new Point(0,1),  new Point(4,10),
						new Point(3,3),  new Point(5,10), };
		PointCluster[] clu = { new PointCluster( pts[0], true), new PointCluster(pts[1], true),
						  	   new PointCluster( pts[2], true), new PointCluster(pts[3], true) 
		};
		
		ps.addPoints(pts);
		ps.addClusters(clu);
		
		for(int i = 0; i < 4; i++){
			clu[i].add(pts[4+i]);	
		}
		
		checkIfCorrectMembership(ps, pts[0], clu[0]);
		checkIfCorrectMembership(ps, pts[1], clu[1]);
		checkIfCorrectMembership(ps, pts[2], clu[2]);
		checkIfCorrectMembership(ps, pts[3], clu[3]);
		checkIfCorrectMembership(ps, pts[4], clu[0]);
		checkIfCorrectMembership(ps, pts[5], clu[1]);
		checkIfCorrectMembership(ps, pts[6], clu[2]);
		checkIfCorrectMembership(ps, pts[7], clu[3]);
		
		Point temp1 = new Point(5,5);
		ps.addPoint(temp1);
		
		int id = ps.getPointMembership(temp1);
		if( id != -1 )
			System.err.println("Error in Test_PointSpace! Incorrect point membership! Got " + id +" expected " + -1 +" " + new Exception().getStackTrace()[0]);
		clu[0].add(temp1);
		checkIfCorrectMembership(ps, temp1, clu[0]);
		clu[0].removePoint(temp1);
		id = ps.getPointMembership(temp1);
		if( id != -1 )
			System.err.println("Error in Test_PointSpace! Incorrect point membership! Got " + id +" expected " + -1 +" " + new Exception().getStackTrace()[0]);
		
		
	}

	private static void checkIfCorrectMembership(PointSpace ps, Point point, PointCluster pointCluster) {
		int pcID = ps.getPointMembership(point);
		
		if( pcID != ps.getClusterID(pointCluster) )
			System.err.println("Error in Test_PointSpace! Incorrect point membership! Got " +ps.getClusterID(pointCluster)  +" expected " + pcID +" " + new Exception().getStackTrace()[1]);
		
		PointCluster pc = ps.getCluster(pcID);
		if( !(pc == pointCluster) )
			System.err.println("Error in Test_PointSpace! Incorrect point membership! Got " + pc +" expected " + pointCluster +" " + new Exception().getStackTrace()[1]);
		
	}

}
*/
