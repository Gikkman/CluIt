package com.cluit.util.io.excel;

import java.io.File;

import com.cluit.util.threading.WorkingThread;

//TODO: Think about this idea. Is it possible to define a common "worker thread" interface? Maybe look into JavaFX's Worker
@SuppressWarnings("unused")
public class DataImporter_Excel extends WorkingThread{
	private FileReader_Excel mExcelFileReader;
	private int				 mExcelFileSheets;	
	private File 			 mExcelFile;
	
	public DataImporter_Excel(File excelFile) {
		mExcelFile = excelFile;
	}
	
	public synchronized void getNumberOfSheets(){
		
	}
	
	@Override
	protected void doWork() {		
	}

	@Override
	protected void doInit() {		
	}
	
	@Override
	protected void doTerminate() {
		super.doTerminate();
		try{
			wait(2000);
		} catch (InterruptedException e){ 
			e.printStackTrace(); 
		}
	}
	
}
