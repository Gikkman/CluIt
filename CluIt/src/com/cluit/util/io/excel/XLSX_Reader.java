package com.cluit.util.io.excel;

import java.io.File;
import java.io.FileInputStream;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSX_Reader {
public enum TypeFilter {DATE, STRING, BLANK, NUMERIC, BOOL, FORMULA};
	
	private final File file;
	
	private XSSFWorkbook workbook;
	private XSSFSheet currentSheet;
	
	private int[] rowFilter;
	private int[] colFilter;
	
	public XLSX_Reader(File file) throws Exception{
		this.file = file;
	}
	
	public XLSX_Reader loadBook() throws Exception{	
		workbook = new XSSFWorkbook( new FileInputStream(file) );
		return this;
	}
	
	public XLSX_Reader loadSheet(int index) throws Exception{
		currentSheet = workbook.getSheetAt(index);
		return this;
	}
	
	public XLSX_Reader setRowFilter(int ... rows){
		rowFilter = rows;
		return this;
	}
	
	public XLSX_Reader setColFilter(int ... cols){
		colFilter = cols;
		return this;
	}
	
	public double[][] getValues() throws Exception{
		XSSFRow row;
		XSSFCell cell;
		
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