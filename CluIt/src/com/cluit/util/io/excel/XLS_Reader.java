package com.cluit.util.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.structures.Pair;

public class XLS_Reader {
	public enum TypeFilter {DATE, STRING, BLANK, NUMERIC, BOOL, FORMULA};
	
	private final File file;
	
	private HSSFWorkbook workbook;
	private HSSFSheet currentSheet;
	
	private int[] rowFilter = new int[0];
	private int[] colFilter = new int[0];
	
	private ArrayList<Pair<String, double[]>> mData = new ArrayList<>();
	
	public XLS_Reader(File file){
		this.file = file;
	}
	
	public XLS_Reader loadBook() {	
		try{
			POIFSFileSystem fs = new POIFSFileSystem( new FileInputStream(file) );
			workbook = new HSSFWorkbook(fs);
		} catch (IOException e) {
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "I/O exception whilst reading the Excel file. Make sure the file isn't used by another process.", e );
			return null;
		} catch( OldExcelFormatException e){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Old Excel format. Format older than 97 is not supported.", e);
			return null;
		} catch (Exception e){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "An unknown error has occured whilst reading the Excel file.", e);
			return null;
		}
		return this;
	}
	
	public XLS_Reader loadSheet(int index){
		mData = new ArrayList<>();
		currentSheet = workbook.getSheetAt(index);	
		HSSFRow row;
		double val;
		
		int rows = currentSheet.getPhysicalNumberOfRows() ;
		int cols = currentSheet.getRow(0).getPhysicalNumberOfCells() ; 
		
		//First, we add the labels to each column. We also create the double[] which will accompany the label. 
		row = currentSheet.getRow(0);
		for(int i = 0; i < cols; i++){
			mData.add(i, new Pair<String, double[]>(row.getCell(i).getStringCellValue(), new double[rows-1]) );
		}
		
		//Iterate through each cell in each row, and fetch the data. We make sure the data-block is rectangular at the same time.
		//For each cell, we assign the value to the correct label, at the correct position
		int y = 1, x = 0;
		try{ 
			for( y = 1; y < rows; y++){
				row = currentSheet.getRow(y);
				if( row.getPhysicalNumberOfCells() != cols){
					throw new IOException();
				}
				for( x = 0; x < cols; x++){
					val = row.getCell(x).getNumericCellValue();
					mData.get(x).r[y-1] = val;
				}
				
			}
		} catch ( NumberFormatException e){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "Error when reading Excel data. The value in cell("+x+","+y+") [x,y] was not a number.", e);
			return null;
		} catch( IOException e){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The excel document "+file+" at page "+index+" is not formated correctly. Make sure that the data block is exactly rectangular. Sometimes Excel has data in visibly empty cells.", e );
			return null;
		}		
				
		return this;
	}
	
	public String[] getLabels(){
		String[] out = new String[mData.size()];
		for(int i = 0; i < mData.size(); i++){
			out[i] = mData.get(i).l;
		}
		return out;
	}
	
	@Deprecated
	public XLS_Reader setRowFilter(int ... rows){
		rowFilter = rows;
		return this;
	}
	
	/**Tells the XLS_Reader to skip certain columns when it creates a feature-matrix. The arguments must be ordered
	 * 
	 * @param cols
	 * @return 
	 */
	public XLS_Reader setColFilter(int ... cols){
		colFilter = cols;
		return this;
	}
	
	public double[][] getValues() {
		if( mData.size() <= 0){
			MethodMapper.invoke(Const.METHOD_EXCEPTION_GENERAL, "The reading of the Excel sheet didn't give any data. Please verify that the Excel document is correctly formated", new IOException());
			return null;
		}
		int rowFilterIndex = 0, colFilterIndex = 0;
		//Each element in mData corresponst to one row. 
		//Since we already know that all rows are of the same lenght, we simply fetch the lenght of row 0
		int cols = mData.size();
		int rows = mData.get(0).r.length;
		
		//To create feature vectors, we have to create rows of doubles out of the columns in mData
		double[][] out = new double[rows - rowFilter.length][cols - colFilter.length];
		double val = 0;
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
				
				val = mData.get(x).r[y];
				out[y-rowFilterIndex][x-colFilterIndex] = val;
			}
		}
		
		return out;
	}
}
