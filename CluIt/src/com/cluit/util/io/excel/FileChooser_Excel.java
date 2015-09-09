package com.cluit.util.io.excel;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public class FileChooser_Excel {
	private final File mLocation;
	
	public FileChooser_Excel(String location){
		mLocation = new File(location);
	}
	
	public File show(Window parent){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Excel file");
		fileChooser.setInitialDirectory( mLocation );
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All files", "*.*"),
				new FileChooser.ExtensionFilter("Excel 97-03", "*.xls"),
				new FileChooser.ExtensionFilter("Excel 2007", "*.xlsx*")
		);		
		File file = fileChooser.showOpenDialog( parent );
		return file;
	}
}
