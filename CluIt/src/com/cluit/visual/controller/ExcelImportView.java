package com.cluit.visual.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.cluit.util.Const;
import com.cluit.util.AoP.MethodMapper;
import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Data;
import com.cluit.util.io.excel.FileChooser_Excel;
import com.cluit.util.io.excel.FileReader_Excel;

public class ExcelImportView implements Initializable{
	@FXML private Button button_load;
	@FXML private Button button_import;
	@FXML private Label  label_load_file;
	@FXML private ScrollPane scroll_pane;
	@FXML private GridPane feature_grid;
	@FXML private ComboBox<String> combo_sheets;
	
	private Node[] feature_grid_basis;
	private ColumnConstraints[] feature_grid_cols;
	private LinkedList<CheckBox> mSelectBoxes = new LinkedList<>();
	
	private File		     mFile;
	private FileReader_Excel mReader;
	private int 			 mSheetIndex = 0;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Cache the appearance for the grid, so we can reset it at a later time
		feature_grid_basis = feature_grid.getChildren().toArray( new Node[0] );
		feature_grid_cols  = feature_grid.getColumnConstraints().toArray( new ColumnConstraints[0] );
		
		//Add a listner that updates the grid view whenever we change value in the combo box (the sheet chooser)
		combo_sheets.valueProperty().addListener( (ov, oldV, newV) -> {
			if( newV != null){
				mSheetIndex = combo_sheets.selectionModelProperty().get().getSelectedIndex();
				buildGrid(mSheetIndex);
			}
		} );
		
		//Link the "OnClose" callback to a method
		MethodMapper.addMethod(Const.METHOD_UNLOAD_EXCEL_SHEET, (args) -> {
			onClose();
		} );
	}	
	
	@FXML protected void button_load_OnClick(ActionEvent event){
		//Create a file chooser dialogue so the user can choose an excel file from his/her file system
		FileChooser_Excel chooser = new FileChooser_Excel(Const.DIR_EXCEL);		
		mFile = chooser.show( button_load.getScene().getWindow() );
		if( mFile == null ){
			//If the user closed the window without choosing a file
			return;	
		}
		loadExcelFile(); 
		fillSheetsComboBox();
	}

	@FXML protected void button_import_OnClick(ActionEvent event) {
		if( mReader == null ){
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, "No data can be imported. Please load a valid Excel file first");
		} else {
			//Create a column filter by looking at which check-boxes the user has ticked
			int[] colFilter = getColFilter(mSelectBoxes);		
			if( colFilter.length > 0)
				mReader.setColFilter(colFilter);
			try{
				//Try to fetch the data from the chosen columns and store it in the VariableSingleton.
				//This might throw an error, if data in one of the cells in the chosen columns isn't numeric. In that case, we don't store
				//anything in the variable singleton (to prevent the user from running a clustering algorithm with no valid data).
				double[][] data = mReader.getDoubleValues();
				String[] labels = mReader.getFilteredLabels();
				VariableSingleton.getInstance().setData( new Data(labels, data) );
			} catch (ClassCastException | IOException e){
				MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, e.getMessage()+"\n\nCould not import data from the specified Excel file. Please double check that all values within the selected data columns are numeric (or calculated) values. The import cannot handle strings, blanks, errors or the similar.\n");
				return;
			}

			button_import.getScene().getWindow().hide();
		}
	}
	
	@FXML protected void feature_grid_OnScroll(ScrollEvent event){
		//Implements horizontal scrolling of the column labels
		int scrollDir= event.getDeltaY() > 0 ? 1 : -1;
		double panePos   = scroll_pane.getHvalue();	
		
		//TODO: Fix this something better. Like a smoother scroll and some logic to know how much to scroll
		scroll_pane.setHvalue( panePos + 0.3 * scrollDir );
	}
	
	@FXML protected void button_refresh_ExcelFile(ActionEvent event){
		unloadWorkbook();
	}
	
	@FXML protected void button_reload_excel(ActionEvent event){
		if( mFile == null ){
			//If there weren't a file loaded yet
			return;	
		}
		loadExcelFile(); 
		fillSheetsComboBox();
	}
	
	private void onClose(){
		unloadWorkbook();
	}
	
	private void unloadWorkbook() {
		if( mReader != null ){
			mReader.unload();
			mReader = null;
			clearGrid();
			clearSheetsComboBox();
		}
	}
	
	private void loadExcelFile(){
		try {
			//TODO: Should probably be a background task. Causes lag atm.
			FileReader_Excel tempReader = FileReader_Excel.create(mFile);
			if( mReader != null ) mReader.unload();
			mReader = tempReader;
			label_load_file.setText(mFile.getPath());
		} catch (EncryptedDocumentException | InvalidFormatException | NumberFormatException | IOException e ) {
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, e.getMessage() );
		} 
	}
	
	private void fillSheetsComboBox(){
		clearSheetsComboBox();
		
		String[] names = mReader.getSheets();
		combo_sheets.getItems().addAll( names);
		combo_sheets.setValue( names[0] );
	}
	
	private void clearSheetsComboBox() {
		combo_sheets.getItems().clear();
		mSheetIndex = 0;
	}

	private int[] getColFilter(LinkedList<CheckBox> mSelectBoxes2) {
		BitSet checked = new BitSet( mSelectBoxes.size() );
		for(int i = 0; i < mSelectBoxes.size(); i++)
			if( mSelectBoxes.get(i).isSelected() )
				checked.set(i);

		int[] colFilter = new int[ mSelectBoxes.size()  - checked.cardinality() ];
		
		if( colFilter.length > 0){
			colFilter[0] = checked.nextClearBit(0);		
			for(int i = 1; i < colFilter.length; i++){
				colFilter[i] = checked.nextClearBit( colFilter[i-1]+1 );		
			}
		}
		
		return colFilter;
	}	
	
	private void clearGrid(){
		feature_grid.getChildren().clear();
		feature_grid.getChildren().addAll(feature_grid_basis);
		feature_grid.getColumnConstraints().clear();
		feature_grid.getColumnConstraints().addAll( feature_grid_cols );
		mSelectBoxes.clear();
	}
	
	private void buildGrid(int sheet) {
		clearGrid();
		
		String[] labels = new String[0];
		try {
			labels = mReader.loadSheet(sheet).getLabels();
		} catch (NumberFormatException|IOException e){
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, e.getMessage() );
			return;
		}
		
		int idx = 2; //The index is the grid that we'll place items at. Idx 0 = the labels, Idx 1 = the separator
		
		createChooser(labels[0], idx++);
		for( int i = 1; i < labels.length; i++){
			createSeparator(idx++);
			createChooser(labels[i], idx++);			
		}
	}

	private void createChooser(String string, int idx) {
		Label label = new Label(string);
		label.setWrapText(true);
		label.setTextAlignment( TextAlignment.CENTER );
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(true);
		
		ColumnConstraints cc = new ColumnConstraints();
		cc.setMinWidth(100);
		cc.setPrefWidth(100);
		cc.setHgrow(Priority.SOMETIMES);
		cc.setFillWidth(true);
		cc.setHalignment(HPos.CENTER);
		
		feature_grid.addColumn( idx, label, checkBox );	
		feature_grid.getColumnConstraints().add(cc);
		
		mSelectBoxes.add(checkBox);
	}

	private void createSeparator(int idx) {
		Separator separator = new Separator();
		GridPane.setRowSpan(separator, 2);	
		separator.setOrientation( Orientation.VERTICAL );
		separator.setHalignment(HPos.CENTER);
		
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(1);
		cc.setHgrow(Priority.NEVER);
		cc.setFillWidth(true);
		cc.setHalignment(HPos.CENTER);
		
		feature_grid.addColumn(idx, separator );
		feature_grid.getColumnConstraints().add(cc);
	}
}
