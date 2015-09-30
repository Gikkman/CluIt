package com.cluit.util.io.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.structures.Pair;

public class FileReader_Excel {
	public enum TypeFilter {DATE, STRING, BLANK, NUMERIC, BOOL, FORMULA};
	
	private Workbook workbook;
	private Sheet currentSheet;
	
	private int[] rowFilter = new int[0];
	private int[] colFilter = new int[0];
	
	private ArrayList<Pair<String, Object[]>> mData = new ArrayList<>();
	
	private FileReader_Excel(){	}
	
	/**Creates an XLS_Reader bound to a specific file.
	 * To get the data from the file, the workbook must first be read. Then, a sheet must be loaded (see {@link #loadSheet(int)}).
	 * 
	 * @param file
	 */
	public static FileReader_Excel create(File file) throws EncryptedDocumentException, InvalidFormatException, IOException{
		if( !file.exists() )
			throw new FileNotFoundException("The requested file "+file+" does not exist!");
		if( !file.isFile())
			throw new FileNotFoundException("The requested file "+file+" is not a file!");
		if( !file.canRead() )
			throw new AccessDeniedException("The requested file "+file+" cannot be read. Access denied!");
		if( !(file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx") || file.getName().endsWith(".xlsm")) )
			throw new FileNotFoundException("The requested file "+file+" is not of a valid file type! The supported types are .xls .xlsx and .xlsm\nMake sure that a .xls are not in a Excel-95 or older format!");

		FileReader_Excel reader = new FileReader_Excel();
		reader.workbook = WorkbookFactory.create(file);
		return reader;
	}
	
	
	
	public FileReader_Excel loadSheet(int index) throws IOException{
		if(index >= workbook.getNumberOfSheets() )
			throw new IOException("The requested sheet "+(index+1)+" is out of bounds. The current workbook only contains "+workbook.getNumberOfSheets()+" sheets");
		
		ArrayList<Pair<String, Object[]>> data = new ArrayList<>();
		currentSheet = workbook.getSheetAt(index);		
		
		Row row;
		Object val;
		
		int rows = currentSheet.getPhysicalNumberOfRows() ;
		int firstRow = currentSheet.getFirstRowNum();
		int cols = 0;
		try{
			cols = currentSheet.getRow( firstRow ).getPhysicalNumberOfCells();
		} catch (NullPointerException e){
			throw new IOException( "The requested sheet does not have any data in it. Please choose another sheet");
		}
		//First, we add the labels to each column. We also create the double[] which will accompany the label.
		row = currentSheet.getRow(firstRow);
		for(int i = 0; i < cols; i++){
			data.add(i, new Pair<String, Object[]>(row.getCell(i).getStringCellValue(), new Object[rows-1]) );
		}
		
		//Iterate through each cell in each row, and fetch the data. We make sure also make sure that each row contains the same amount of elements
		//For each cell, we assign the value to the correct label, at the correct position
		//
		//The wierd y - value shenanigans here is to compensate for the case where the data doesn't begin on the very top row
		int y = 1 + firstRow, x = 0;
		try{ 
			for( ; y < rows+firstRow; y++){
				row = currentSheet.getRow(y);
				if( row.getPhysicalNumberOfCells() != cols){
					throw new IOException("The number of cells in row "+(y+1+firstRow)+" ("+row.getPhysicalNumberOfCells()+") does not match the assumed number of cells ("+cols+").\nPlease make sure that the data in the desired sheet block is exactly rectangular.");
				}
				for( x = 0; x < cols; x++){
					val = getCellValue( row.getCell(x) );
					data.get(x).right[y-1-firstRow] = val;
				}			
			}
		} catch ( IOException e){
			throw e;
		} 	
				
		mData = data;
		return this;
	}
	
	private Object getCellValue(Cell cell) {
		switch( cell.getCellType() ){
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_ERROR:
			return getCellErrorType( cell );
		case Cell.CELL_TYPE_FORMULA: 
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		default:
			return null;			
		}
	}

	private String getCellErrorType(Cell cell) {
		FormulaError error = FormulaError.forInt( cell.getErrorCellValue() );
		switch( error ){
		case CIRCULAR_REF:
			return "ERR! CIRCULAR_REF";
		case DIV0:
			return "ERR! DIV_BY_0";
		case FUNCTION_NOT_IMPLEMENTED:
			return "ERR! FUNC_NOT_IMPL";
		case NA:
			return "ERR! VAL_NOT_AVAIL";
		case NAME:
			return "ERR! NAME_NOT_DEF";
		case NULL:
			return "ERR! NULL_VALUE";
		case NUM:
			return "ERR! FUNC_ARG_INVAL";
		case REF:
			return "ERR! CELL_REF_INVAL";
		case VALUE:
			return "ERR! ARG_VAL_INVAL";
		default:
			return "ERR! Unknown";
		}
	}

	/**The returned String[] can be empty if no data's been loaded by the XLS_Reader.
	 * 
	 * @return
	 */
	public String[] getLabels(){
		String[] out = new String[mData.size()];
		for(int i = 0; i < mData.size(); i++){
			out[i] = mData.get(i).left.replace("\n", " ");
		}
		return out;
	}
	
	/**Returns a String[] containing the labels of the non-filtered columns.
	 * The returned String[] can be empty if no data's been loaded by the XLS_Reader.
	 * 
	 * @return
	 */
	public String[] getFilteredLabels() {
		ArrayList<String> out = new ArrayList<>();
		String[] labels = getLabels();
		int colFilterIndex = 0;
		for( int i = 0; i < mData.size(); i++){
			if( colFilterIndex < colFilter.length && colFilter[colFilterIndex] == i){
				colFilterIndex++;
				continue;
			}
			out.add( labels[i] );
		}
		return out.toArray( new String[0] );
	}
	
	@Deprecated
	public FileReader_Excel setRowFilter(int ... rows){
		rowFilter = rows;
		return this;
	}
	
	/**Tells the XLS_Reader to skip certain columns when it creates a feature-matrix. The arguments must be ordered in ascending order. A filter remains set. To clear the filter, call this function without any arguments
	 * 
	 * @param cols
	 * @return 
	 */
	public FileReader_Excel setColFilter(int ... cols){
		int high = -1;
		for(int val : cols ){
			if( val > high )
				high = val;
			else{
				MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The column filter must be an ordered array of positive integers in ascending order!", new Exception() );
				return null;
			}				
		}
		colFilter = cols;
		return this;
	}
	
	public int[] getColFilter(){
		return colFilter;
	}
	
	public Object[][] getValues() {
		if( mData.size() <= 0){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The reading of the Excel sheet didn't give any data. Please verify that the Excel document is correctly formated", new IOException());
			return null;
		}
		int rowFilterIndex = 0, colFilterIndex = 0;
		//Each element in mData corresponst to one row. 
		//Since we already know that all rows are of the same lenght, we simply fetch the lenght of row 0
		int cols = mData.size();
		int rows = mData.get(0).right.length;
		
		//To create feature vectors, we have to create rows of doubles out of the columns in mData
		Object[][] out = new Object[rows - rowFilter.length][cols - colFilter.length];
		Object val = 0;
		for(int y = 0; y < rows; y++){	
			if( rowFilterIndex < rowFilter.length && y == rowFilter[rowFilterIndex] ){
				rowFilterIndex++;
				continue;
			}
			
			colFilterIndex = 0;
			for(int x = 0; x < cols; x++){
				if( colFilterIndex < colFilter.length && x == colFilter[colFilterIndex] ){
					colFilterIndex++;
					continue;
				}
				
				val = mData.get(x).right[y];
				out[y-rowFilterIndex][x-colFilterIndex] = val;
			}
		}
		
		return out;
	}
	
	public double[][] getDoubleValues() throws ClassCastException, IOException{
		if( mData.size() <= 0){
			throw new IOException("The reading of the Excel sheet didn't give any data. Please verify that the Excel document is correctly formated");
		}
		int rowFilterIndex = 0, colFilterIndex = 0;
		//Each element in mData corresponst to one row. 
		//Since we already know that all rows are of the same lenght, we simply fetch the lenght of row 0
		int cols = mData.size();
		int rows = mData.get(0).right.length;
		
		//To create feature vectors, we have to create rows of doubles out of the columns in mData
		double[][] out = new double[rows - rowFilter.length][cols - colFilter.length];
//		if( out.length == 0 || out[0].length == 0 )
//			throw new IOException("The column filter cancels out all data. Nothing can be loaded");
		
		double val = 0;
		for(int y = currentSheet.getFirstRowNum(); y < rows; y++){	
			if( rowFilterIndex < rowFilter.length && y == rowFilter[rowFilterIndex] ){
				rowFilterIndex++;
				continue;
			}
			
			colFilterIndex = 0;
			for(int x = 0; x < cols; x++){
				if( colFilterIndex < colFilter.length && x == colFilter[colFilterIndex] ){
					colFilterIndex++;
					continue;
				}
				try{
					Object obj = mData.get(x).right[y];
					
					if( obj.equals( "" ) ) //If the data in excel was NULL, it comes out as "". In that case, we interpret it as 0
						val = 0;	
					else
						val = (double) obj;

				} catch(ClassCastException e){
					@SuppressWarnings("unused")
					Object obj = mData.get(x).right[y];
					throw new ClassCastException("Cannot convert the value in row "+(y+2)+" column "+(x+1)+" to a double. Please check your data block");
				}
				out[y-rowFilterIndex][x-colFilterIndex] = val;
			}
		}
		
		return out;
	}

	/**Releases the file handle on the workbook
	 */
	public void unload() {
		if( workbook != null){
			try {
				workbook.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		workbook = null;
		currentSheet = null;
	}

	/**Returns an array of strings, which are the names of all sheets. Might return NULL, if the workbook was not loaded correctly
	 */
	public String[] getSheets() {
		if( workbook == null )
			return null;
		
		String[] names = new String[ workbook.getNumberOfSheets() ];
		for(int i = 0; i < names.length; i++){
			names[i] = workbook.getSheetName(i);
		}
		return names;
	}
}
