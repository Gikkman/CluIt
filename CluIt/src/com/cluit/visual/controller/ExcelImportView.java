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
	private FileReader_Excel mReader;
	private LinkedList<CheckBox> mSelectBoxes = new LinkedList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		feature_grid_basis = feature_grid.getChildren().toArray( new Node[0] );
		feature_grid_cols  = feature_grid.getColumnConstraints().toArray( new ColumnConstraints[0] );
	}	
	
	@FXML protected void button_load_OnClick(ActionEvent event){
		System.out.println("LOAD");
		FileChooser_Excel chooser = new FileChooser_Excel(Const.DIR_EXCEL);		
		File file = chooser.show( button_load.getScene().getWindow() );
		if( file == null )
			return;
	
		try {
			//TODO: Should probably be a background task. Causes lag atm. Also, load which sheets are availible and use those to build, if possible
			FileReader_Excel tempReader = FileReader_Excel.create(file);
			mReader = tempReader;
			label_load_file.setText(file.getPath());
			clearGrid();
			buildGrid();
		} catch (EncryptedDocumentException | InvalidFormatException | NumberFormatException | IOException e ) {
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, e.getMessage() );
		}  
	}

	@FXML protected void button_import_OnClick(ActionEvent event) {
		if( mReader == null ){
			MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, "No data can be imported. Please load a valid Excel file first");
		} else {
			int[] colFilter = getColFilter(mSelectBoxes);		
			if( colFilter.length > 0)
				mReader.setColFilter(colFilter);
			
			try{
				double[][] data = mReader.getDoubleValues();
				VariableSingleton.getInstance().putObject(Const.V_IMPORTED_EXCEL_DATA, data);
			} catch (ClassCastException | IOException e){
				MethodMapper.invoke(Const.METHOD_INFORMATION_EXCEL, e.getMessage()+"\n\nCould not import data from the specified Excel file. Please double check that all values within the selected data columns are numeric (or calculated) values. The import cannot handle strings, blanks, errors or the similar.\n");
				return;
			}
			
			//TODO: Suspend the button for a  short while before hiding the window, for cool FXs
			button_import.getScene().getWindow().hide();
		}
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

	@FXML protected void feature_grid_OnScroll(ScrollEvent event){
		int scrollDir= event.getDeltaY() > 0 ? 1 : -1;
		double panePos   = scroll_pane.getHvalue();	
		//TODO: Fix this something better. Like a smoother scroll and some logic to know how much to scroll
		scroll_pane.setHvalue( panePos + 0.3 * scrollDir );
	}
	
	private void clearGrid(){
		feature_grid.getChildren().clear();
		feature_grid.getChildren().addAll(feature_grid_basis);
		feature_grid.getColumnConstraints().clear();
		feature_grid.getColumnConstraints().addAll( feature_grid_cols );
		mSelectBoxes.clear();
	}
	
	private void buildGrid() throws NumberFormatException, IOException {
		String[] labels = mReader.loadSheet(0).getLabels();
		int idx = 2;

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
