package com.cluit.util.io.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class XLS_Reader {
	public enum TypeFilter {DATE, STRING, BLANK, NUMERIC, BOOL, FORMULA};
	
	private final File file;
	
	private HSSFWorkbook workbook;
	private HSSFSheet currentSheet;
	
	private int[] rowFilter;
	private int[] colFilter;
	
	public XLS_Reader(File file) throws Exception{
		this.file = file;
	}
	
	public XLS_Reader loadBook() throws Exception{
		try{
			POIFSFileSystem fs = new POIFSFileSystem( new FileInputStream(file) );
			workbook = new HSSFWorkbook(fs);
		} catch (IOException e) {
			throw e;
		} catch( OldExcelFormatException e){
			throw e;
		}
		return this;
	}
	
	public XLS_Reader loadSheet(int index) throws Exception{
		currentSheet = workbook.getSheetAt(index);
		return this;
	}
	
	public XLS_Reader setRowFilter(int ... rows){
		rowFilter = rows;
		return this;
	}
	
	public XLS_Reader setColFilter(int ... cols){
		colFilter = cols;
		return this;
	}
	
	public double[][] getValues() throws Exception{
		HSSFRow row;
		HSSFCell cell;
		
		int rowFilterIndex = 0, colFilterIndex = 0;

		//The -1 in cols stems from the fact that one collumn act as the "filter col", i.e. the collum
		//which values decides whether we consult that row or not
		int cols = currentSheet.getRow(0).getPhysicalNumberOfCells() ; 
		int rows = currentSheet.getPhysicalNumberOfRows() ;
		
		double[][] out = new double[rows - rowFilter.length][cols - colFilter.length];
		
		for(int y = 0; y < rows; y++){	
			if( rowFilterIndex < rowFilter.length && y == rowFilter[rowFilterIndex] ){
				rowFilterIndex++;
				continue;
			}
			
			row = currentSheet.getRow(y);
			colFilterIndex = 0;
			for(int x = 0; x < cols; x++){
				if( colFilterIndex < colFilter.length && x == colFilter[colFilterIndex] ){
					colFilterIndex++;
					continue;
				}
				
				cell = row.getCell(x);
				out[y-rowFilterIndex][x-colFilterIndex] = cell.getNumericCellValue();
			}
		}
		
		return out;
	}
}
