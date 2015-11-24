package testing.io;

import java.io.File;
import java.io.IOException;

import com.cluit.util.io.excel.FileReader_Excel;
import com.cluit.util.methods.MiscUtils;

import testing._helpers.tsHelp;

public class Test_ExcelReader {
	private static String path = "resources/testing/";
	private static String[] labelFacit = {"First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh" };
	private static Object[] valueFacit12 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
	private static Object[] valueFacit3 = {1, 'A', 1, 5};

	public static void run() {
		System.out.println("Test - ExcelReader initialized");
				
		FileReader_Excel r = testCreate();
		testLoadSheet(r);
		testGetLabels(r);
		testSetColFilter(r);
		testGetValues(r);
		
		try {
			r = FileReader_Excel.create( new File(path + "test_book.xlsx") );
			testLoadSheet(r);
			testSetColFilter(r);
			testGetLabels(r);
			testGetValues(r);
		} catch (Exception e) {
			System.err.println("Error in Test - ExcelReader. Could not create book test_book.xlsx" + MiscUtils.getStackPos());
		}
		
		System.out.println("Test - ExcelReader finalized");
	}
	
	@SuppressWarnings("unused")
	private static FileReader_Excel testCreate() {
		FileReader_Excel test_noFile;
		try {
			test_noFile = FileReader_Excel.create( new File(path + "noFile") );
			System.err.println("Error in Test - ExcelReader. Loading an empty file did not throw an exception!" + MiscUtils.getStackPos() );
		} catch (Exception e) { 	}
		
		FileReader_Excel test_error;
		try {
			test_error = FileReader_Excel.create( new File(path + "test_error.xls") );
			System.err.println("Error in Test - ExcelReader. Loading an erroneous book did not throw an exception!" + MiscUtils.getStackPos() );
		} catch (Exception e) {		}
		
		FileReader_Excel test_old;
		try {
			test_old = FileReader_Excel.create( new File(path + "test_book_95.xls") );
			System.err.println("Error in Test - ExcelReader. Loading an old book did not throw an exception!" + MiscUtils.getStackPos() );
		} catch (Exception e) {		}
		
		FileReader_Excel test_correct = null;
		try {
			test_correct = FileReader_Excel.create( new File(path + "test_book.xls") );
		} catch (Exception e) {
			System.err.println("Error in Test - ExcelReader. Loading a correct book did throw an exception!" + MiscUtils.getStackPos() );
			e.printStackTrace();
		}
		return test_correct;
	}
	
	private static void testLoadSheet(FileReader_Excel r) {
		try {
			r.loadSheet(5);
			System.err.println("Error in Test - ExcelReader. Loading an out-of-index sheet did not cause an error!" + MiscUtils.getStackPos() );
		} catch (NumberFormatException e1) {
			System.err.println("Error in Test - ExcelReader. Loading an out-of-index sheet threw wrong exception!" + MiscUtils.getStackPos() );
			e1.printStackTrace();
		} catch (IOException e1) {		}
			
		try {
			r.loadSheet(4);
			System.err.println("Error in Test - ExcelReader. Loading a sheet with wrong shape didn't cause an error!" + MiscUtils.getStackPos() );
		} catch (NumberFormatException e1) {
			System.err.println("Error in Test - ExcelReader. Loading a sheet with wrong shape threw wrong exception!" + MiscUtils.getStackPos() );
			e1.printStackTrace();
		} catch (IOException e1) {	}
		
		try {
			r.loadSheet(3);
			System.err.println("Error in Test - ExcelReader. Loading a sheet with wrong shape didn't cause an error!" + MiscUtils.getStackPos() );
		} catch (NumberFormatException e1) {
			System.err.println("Error in Test - ExcelReader. Loading a sheet with wrong shape threw wrong exception!" + MiscUtils.getStackPos() );
			e1.printStackTrace();
		} catch (IOException e1) {
		}
		
			
		try{
			if( r.loadSheet(2) != r){
				System.err.println("Error in Test - ExcelReader. Loading a correct sheet did cause an error!" + MiscUtils.getStackPos() );
			}
			if( r.loadSheet(1) != r){
				System.err.println("Error in Test - ExcelReader. Loading a correct sheet did cause an error!" + MiscUtils.getStackPos() );
			}
			if( r.loadSheet(0) != r){
				System.err.println("Error in Test - ExcelReader. Loading a correct sheet did cause an error!" + MiscUtils.getStackPos() );
			}
		}  catch (Exception e) {
			System.err.println("Error in Test - ExcelReader. Unwanted "+e.getClass()+" thrown!" +MiscUtils.getStackPos() );
			e.printStackTrace();
		}	
	}
	
	private static void testSetColFilter(FileReader_Excel r) {
		if( r.setColFilter(-1, 0, 1) != null ){
			System.err.println("Error in Test - ExcelReader. Setting an negative colFilter did not cause an error!" + MiscUtils.getStackPos() );
		}
		if( r.getColFilter().length > 0) {
			System.err.println("Error in Test - ExcelReader. A colFilter should not be set at the moment!" + MiscUtils.getStackPos() );
		}
		
		if( r.setColFilter(0,2,1) != null ){
			System.err.println("Error in Test - ExcelReader. Setting an unordered colFilter did not cause an error!" + MiscUtils.getStackPos() );
		}
		if( r.getColFilter().length > 0) {
			System.err.println("Error in Test - ExcelReader. A colFilter should not be set at the moment!" + MiscUtils.getStackPos() );
		}
		
		int[] colFilter = {0, 1, 2};
		if( r.setColFilter(colFilter) != r ){
			System.err.println("Error in Test - ExcelReader. Setting a correct colFilter caused an error!" + MiscUtils.getStackPos() );
		}
		if( !tsHelp.ArraysMatch(colFilter, r.getColFilter() ) )
			System.err.println("Error in Test - ExcelReader. Fetching a set colFilter returned an erroneous array! Expected "+tsHelp.atos(colFilter)+" Got "+tsHelp.atos(r.getColFilter()) + MiscUtils.getStackPos() ) ;	
	}
	
	private static void testGetLabels(FileReader_Excel r) {
		try {
			String[] labels = r.loadSheet(0).getLabels();
			checkLabels(labels);
			
			labels = r.loadSheet(1).getLabels();
			checkLabels(labels);
			
			labels = r.loadSheet(2).getLabels();
			checkLabels(labels);
		} catch (NumberFormatException | IOException e) {
			System.err.println("Error in Test - ExcelReader. Unwanted "+e.getClass()+" thrown!" +MiscUtils.getStackPos() );
			e.printStackTrace();
		}
	}
	
	private static void checkLabels(String[] labels) {
		for(int i = 0; i < labels.length; i++)
			if( !labels[i].equals(labelFacit[i] ) )
					System.err.println("Error in Test - ExcelReader. Wrong label found. Expected "+labelFacit[i]+" Got "+labels[i]+MiscUtils.getStackPos());;
	}

	private static void testGetValues(FileReader_Excel r) {
		try{
		Object[][] values = r.loadSheet(0).setColFilter(0, 3, 6).getValues();		
		
		int idx = 0;
		for( int x = 0; x < values.length; x++)
			for( int y = 0; y < values[x].length; y++)
				if( values[x][y].equals( valueFacit12[idx++]) )
					System.err.println("Missmatch at ["+x+","+y+"] Expected "+valueFacit12[idx-1]+" Found "+values[x][y] + " " + MiscUtils.getStackPos());
		
		values = r.loadSheet(1).setColFilter(0, 3, 6).getValues();		
		idx = 0;
		for( int x = 0; x < values.length; x++)
			for( int y = 0; y < values[x].length; y++)
				if( values[x][y].equals(valueFacit12[idx++]) )
					System.err.println("Missmatch at ["+x+","+y+"] Expected "+valueFacit12[idx-1]+" Found "+values[x][y] + " " + MiscUtils.getStackPos());
		
		values = r.loadSheet(2).setColFilter().getValues();
		idx = 0;
		for( int x = 0; x < values.length; x++)
			for( int y = 0; y < values[x].length; y++)
				if( values[x][y].equals(valueFacit3[idx++]) )
					System.err.println("Missmatch at ["+x+","+y+"] Expected "+valueFacit3[idx-1]+" Found "+values[x][y] + " " + MiscUtils.getStackPos());
	
		} catch( Exception e){
			System.err.println("Error in Test - ExcelReader. Unwanted "+e.getClass()+" thrown!" +MiscUtils.getStackPos() );
			e.printStackTrace();
		}
	}
}
