package com.cluit.visual.application.view.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.cluit.util.dataTypes.Results;
import com.cluit.util.structures.Pair;

public class TableTabController extends _AbstractTableTab{	
	private final int LABELS_COLUMN = 0, HEADER_ROW = 0, CLUSTER_NAME_ROW = 1, HORIZONTAL_SEPARATOR_ROW = 2, DATA_ROW_BEGINS = 3,
					  DECIMALS_KEPT = 2;
	private final boolean HAS_TEXT = true, HAS_NO_TEXT = false;
	
	@FXML Group group;
	HBox wrap_pane;
	
	private final boolean DEBUG_MODE = false;
	private final HashMap<String, Integer> label_to_row = new HashMap<>();
	private final ArrayList< GridPane > mPanes = new ArrayList<>();
	
	private boolean first = true;
	private GridPane mLabelGrid;
	private int 	 mNextLabelRow = 0; 	
	private int 	 mNextRun = 1;

	@FXML protected void clear_tab(ActionEvent ae){
		group.getChildren().clear();
		wrap_pane = new HBox();
		group.getChildren().add(wrap_pane);
		
		label_to_row.clear();
		mNextLabelRow = 0;
		mNextRun = 1;
		
		first = true;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		wrap_pane = new HBox();
		group.getChildren().add(wrap_pane);
	}

	@Override
	protected String getTabID() {
		return "tableTabController";
	}
	
	@Override
	protected void paintThisTab(Results r) {
		if( first )
			createBaseLayout();
		
		VBox vbox = new VBox();
		vbox.setAlignment( Pos.CENTER );
		
		GridPane grid = new GridPane();
		commonGridSetup(grid, r.numberOfClusters(), HAS_TEXT);
		
		int[] labelIndices = getLabelIndices( r.getLabels() );	
		grid.getChildren().addAll( 	getDataLabels(r, labelIndices) );
		grid.getChildren().add( 	getClearButton( vbox, r.numberOfClusters() ) );
		
		vbox.getChildren().add(grid);	
		if( r.hasMiscData() )
			vbox.getChildren().addAll( getMiscDataLabels(r) );
		
		wrap_pane.getChildren().add(vbox);
		mPanes.add(  grid );
	}

	private Button getClearButton(VBox vbox, int numberOfClusters) {
		Button button = new Button("X");
		button.setMaxSize(5, 5);		
		button.setFont( new Font(4) );
		
		GridPane.setConstraints(button, numberOfClusters - 1, 0, 1, 1, HPos.RIGHT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		
		button.setOnAction( (ae) -> vbox.getChildren().clear() );
		
		return button;
	}

	/**Fetches the row indices for the entries data labels
	 * 
	 * @param labels The name of the fields for which we are searching indices
	 * @return
	 */
	private int[] getLabelIndices(String[] labels) {
		int[] indices = new int[ labels.length ];
		Integer index = null;
		
		for( int i = 0; i < labels.length; i++ ){
			index = label_to_row.get( labels[i] );
			if( index == null ){
				index = addLabel( labels[i] );	
			}	
			indices[i] = index;
		}
		
		return indices;
	}
	
	/**Creates the data labels, that can be painted into the HBOX
	 * 
	 * @param r The result data
	 * @param labelIndices The index-array that we got from {@link TableTabController.getLabelIndices }
	 * @return
	 */
	private ArrayList<Label> getDataLabels(Results r, int[] labelIndices) {
		ArrayList<Label> dataLabels = new ArrayList<>();
		
		//First we add all the data labels
		for( int col = 0; col < r.numberOfClusters(); col++ ){
			double[] data = r.getCentoid(col);
			for( int row = 0; row < data.length; row++ ){
				dataLabels.add( getDataCell(labelIndices[row], col, data[row] ) );
			}
		}
		
		Arrays.sort(labelIndices);
		//Then, we add empty labels for those rows that does not contain data
		int row = 0, index = 0;
		while( row < mNextLabelRow ){
			if( index < labelIndices.length && labelIndices[index] == row ){
				index++;
			}
			else {
				dataLabels.add( getEmptyRow(row) );
			}
			row++;
		}
		
		return dataLabels;
	}
	
	private ArrayList<Label> getMiscDataLabels(Results r) {
		ArrayList<Label> miscDataLabels = new ArrayList<>();
		ArrayList<Pair<String, Double>> miscData = r.getMiscData();
		
		miscDataLabels.add(new Label(" ") ); //Adds an empty row
		
		for( int i = 0; i < miscData.size(); i++){
			Pair<String, Double> data = miscData.get(i);
			
			Label label = new Label(data.l +": " + data.r);
			
			miscDataLabels.add(label);
		}
		
		return miscDataLabels;
	}

	/**************************************************************************************************************************/
	/**************************************************************************************************************************/
	/**************************************************************************************************************************/
	
	private void createBaseLayout(){
		mLabelGrid = new GridPane();
		mLabelGrid.setMinWidth( 50 );
		mLabelGrid.setMaxWidth( 100 );
		commonGridSetup( mLabelGrid, 1, HAS_NO_TEXT );		
				
		wrap_pane.getChildren().add(mLabelGrid);
		first = false;
	}
	
	private void commonGridSetup(GridPane grid, int columnsToSpan, boolean createText) {
		setupGaps( grid );
		grid.getChildren().add(    getMainHeader(columnsToSpan, createText) ); 
		grid.getChildren().addAll( getSubHeaders(columnsToSpan, createText) ); 
		grid.getChildren().add( getHorizontalSeparator(HORIZONTAL_SEPARATOR_ROW) );
		grid.getChildren().add( getVerticalSeparator( columnsToSpan ) );
		
		grid.setGridLinesVisible(DEBUG_MODE);
	}

	private void setupGaps(GridPane grid) {
		grid.setHgap(10);
		grid.setVgap(2);
	}
	
	private int addLabel(String field) {
		Label label = new Label(field);
		label.setPadding( new Insets(0,0,0,10));
		GridPane.setConstraints(label, LABELS_COLUMN, mNextLabelRow + DATA_ROW_BEGINS, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
		
		mLabelGrid.getChildren().add(label);
		
		label_to_row.put(field, mNextLabelRow);
		
		//Add blank spacing to already existing Grids. This'll move the misc-info downwards
		for( GridPane pane : mPanes ){
			Label blank = new Label(" ");
			GridPane.setConstraints(blank, 0, mNextLabelRow+DATA_ROW_BEGINS, 1, 1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER);
			pane.getChildren().add(blank);
		}
		
		return mNextLabelRow++;
	}
	
	private Label getDataCell(int row, int column, double data){
		String value = String.valueOf(data);
		
		//This logic checks if we can keep up to the desired number of decimals, or if that would move us outside the string array
		int decimalPos = value.indexOf(".") + DECIMALS_KEPT + 1 ;
		int substringPoint = decimalPos > value.length() ? value.length() : decimalPos;		
		value = value.substring(0, substringPoint);
		
		Label n = new Label(value);
		GridPane.setConstraints(n, column, row + DATA_ROW_BEGINS, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER );
		return n;
	}
	
	private Label getEmptyRow(int row){
		Label n = new Label(" ");
		GridPane.setConstraints(n, 0, row + DATA_ROW_BEGINS, GridPane.REMAINING, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER );
		return n;
	}

	/**Creates the main header (i.e. the heading saying "Run X")
	 * 
	 * @param columnsToSpan How many sub headings will here be (indicates how many columns this heading has to span)
	 * @return
	 */
	private Label getMainHeader(int columnsToSpan, boolean text) {
		Label heading = new Label(text ? "Run "+ mNextRun++ : " ");
		
		GridPane.setConstraints(heading, 0, HEADER_ROW, columnsToSpan, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(8) );
		return heading;
	}

	/**Creates the cluster headings (i.e. the headings saying "Cluster 1  Cluster 2  ...  Cluster n" )
	 * 
	 * @param columnsToSpan How many clusters were there, and how many sub headings do we have to create
	 * @return
	 */
	private Label[] getSubHeaders(int amount, boolean text) {
		Label[] subHeadings = new Label[amount];
		
		for( int i = 0; i < amount; i++){
			subHeadings[i] = new Label(text ? "Cluster " + i : " ");
			GridPane.setConstraints(subHeadings[i], i, CLUSTER_NAME_ROW, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(3) );
		}
		return subHeadings;
	}

	/**Creates a horizontal separator, spanning all available columns
	 * 
	 * @param row Which row should the separator be added to
	 * @return
	 */
	private Separator getHorizontalSeparator(int row) {
		Separator separator = new Separator();	
		GridPane.setConstraints(separator, 0, row, GridPane.REMAINING,  1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(3, 0, 3, 0) );
		return separator;
	}
	
	/**Creates a vertical separator, spanning all available rows
	 * 
	 * @param column Which column should the separator be added to
	 * @return
	 */
	private Separator getVerticalSeparator(int column){
		Separator separator = new Separator( Orientation.VERTICAL );
		GridPane.setConstraints(separator, column, 0, 1,  GridPane.REMAINING, HPos.LEFT, VPos.TOP, Priority.NEVER, Priority.NEVER, new Insets(0, 5, 0, 5) );
		return separator;
	}

	static int idx = 1;
	@Override
	protected void DEBUG_paintThisTab(int i) {
		if( first )
			createBaseLayout();
		
		GridPane g = new GridPane();
		commonGridSetup(g, idx++, true);
		wrap_pane.getChildren().add(g);
	}
}
