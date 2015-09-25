package com.cluit.visual.widget;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.HashMap;

import com.cluit.util.methods.MiscUtils;
import com.cluit.util.structures.Pair;

public class DataTable extends GridPane {

	//*******************************************************************************************************
	//region								VARIABLES 		
	//*******************************************************************************************************
	
	private static final String DATA_LABEL = "datalabel";
	private static final int H_GAP = 10, V_GAP = 2, MIN_WIDHT = 50,
							 MAIN_HEADER_ROW = 0, SUB_HEADER_ROW = 1, HORIZONTAL_SEPARATOR_ROW = 2, DATA_LABELS_BEGIN = 3,
						     DECIMAL_PRECISION = 4;
	
	private final int mColumns;
	private final HashMap<String, Integer> mLabelToRow = new HashMap<>();
	
	private int mRows = 0;
	
	//endregion *********************************************************************************************
	//region								CONSTRUCTORS 	
	//*******************************************************************************************************

	public DataTable(String mainHeader, String ... subHeaders){
		super();		
		this.mColumns = subHeaders.length;
		
		setupGridProperties();
		
		add( getMainHeader(mainHeader ) );
		add( getSubHeaders(subHeaders) );
		add( getHorizontalSeparator(HORIZONTAL_SEPARATOR_ROW) );
	}
	
	public DataTable() {
		super();		
		this.mColumns = 1;
		
		setupGridProperties();
		
		add( getHorizontalSeparator(HORIZONTAL_SEPARATOR_ROW) );
	}
	
	//endregion *********************************************************************************************
	//region								STATIC			
	//*******************************************************************************************************
	
	private void setupGridProperties() {
		super.setHgap(H_GAP);
		super.setVgap(V_GAP);
		
		HBox.setHgrow(this, Priority.NEVER);
		VBox.setVgrow(this, Priority.NEVER);

		super.setMinWidth(MIN_WIDHT);
		
		super.setGridLinesVisible(true);
	}

	public static void skinDataLabel(Label label){
		label.getStyleClass().add(DATA_LABEL);
		GridPane.setColumnSpan(label, GridPane.REMAINING);
		GridPane.setHgrow(label, Priority.NEVER);
		GridPane.setVgrow(label, Priority.NEVER);
		GridPane.setHalignment(label, HPos.CENTER);
	}
	
	//endregion *********************************************************************************************
	//region								PUBLIC 			
	//*******************************************************************************************************
	
	public void setLabelRowRelationship(Collection<Pair<String, Integer>> relations){
		for( Pair<String, Integer> p : relations )
			mLabelToRow.put( p.left, p.right);
	}
	
	public void addLabelRowRelationship(Pair<String, Integer> relation){
		mLabelToRow.put( relation.left, relation.right);
	}
	
	public void setColumnData(int column, int totalNumberOfLabels, Collection<Pair<String, String>> data){
		//We first fill the column with blank data. This is simply to create the correct spacing in the table
		for(int i = 0; i < totalNumberOfLabels; i++){
			addDataLabel(column, i, " ");
		}
		//Then, we add the actual data to the table
		for( Pair<String, String> pair : data ){
			setColumnData(column, pair);
		}
	}
	
	
	public void setColumnData(int column, Pair<String, String> data){
		int row = mLabelToRow.getOrDefault(data.left, -1);
		if( row != -1 )
			addDataLabel(column, row, data.right);
		else
			System.err.println("Error. No relationship mapped for " + data.left + MiscUtils.getStackPos());
	}
	
	
	/**Creates a row at the bottom of the table with the parameter text
	 */
	public void appendText(String text) {
		Label label = new Label(text);
				
		skinDataLabel(label);
		GridPane.setConstraints(label, 0, mRows, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER );
		add(label);
		
		
		increaseRowCount();
	}

	/**Creates an empty row at the bottom of the table
	 */
	public void addEmptyRow(){
		appendText(" ");
	}
	
	//endregion *********************************************************************************************
	//region								PRIVATE 		
	//*******************************************************************************************************
	private void add(Node node){
		super.getChildren().add(node);
		
		
	}
	
	private void add(Node ... nodes){
		super.getChildren().addAll(nodes);
	}
	
	/**Creates the main header (i.e. the heading saying "Run X")
	 * 
	 * @return
	 */
	private Label getMainHeader(String mainHeader ) {
		Label heading = new Label(mainHeader);
		skinDataLabel(heading);	
		GridPane.setConstraints(heading, 0, MAIN_HEADER_ROW, mColumns, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(8) );
		
		increaseRowCount();
		return heading;
	}
	
	/**Creates the cluster headings (i.e. the headings saying "Cluster 1  Cluster 2  ...  Cluster n" )
	 * 
	 * @return
	 */
	private Label[] getSubHeaders(String[] subHeaders) {
		Label[] subHeaderLabels = new Label[ subHeaders.length ];
		
		for( int i = 0; i < subHeaders.length; i++){
			subHeaderLabels[i] = new Label(subHeaders[i]);
			skinDataLabel(subHeaderLabels[i]);
			GridPane.setConstraints(subHeaderLabels[i], i, SUB_HEADER_ROW, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(3) );
		}
		
		increaseRowCount();
		return subHeaderLabels;
	}
	
	/**Creates a horizontal separator to a given row, spanning all available columns
	 * 
	 * @return
	 */
	private Separator getHorizontalSeparator(int row) {
		Separator separator = new Separator();	
		GridPane.setConstraints(separator, 0, row, GridPane.REMAINING,  1, HPos.LEFT, VPos.CENTER, Priority.NEVER, Priority.NEVER, new Insets(3, 0, 3, 0) );
		
		increaseRowCount();
		return separator;
	}
	
	private void addDataLabel(int column, int row, String data){		
		//This logic checks if we can keep up to the desired number of decimals, or if that would move us outside the string array
		//I.e. are there enough decimals (it might be say 0.25, and then we can't keep 4 decimals)
		int decimalPos = data.indexOf(".") + DECIMAL_PRECISION + 1 ; //+1, since we want to keep the "." as well
		if( decimalPos != -1 ) {
			int substringPoint = decimalPos > data.length() ? data.length() : decimalPos;		
			data = data.substring(0, substringPoint);
		}
		
		Label label = new Label(data);
		DataTable.skinDataLabel(label);
		
		GridPane.setConstraints(label, column, row + DATA_LABELS_BEGIN, 1, 1, HPos.CENTER, VPos.CENTER, Priority.NEVER, Priority.NEVER );
		add(label);
	}
	
	private void increaseRowCount() {
		mRows++;
	}

	public void setTotalLabelCount(int size) {
		mRows += size;
	}
	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
