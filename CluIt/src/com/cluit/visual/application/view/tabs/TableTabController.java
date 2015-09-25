package com.cluit.visual.application.view.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
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
import java.util.HashMap;
import java.util.ResourceBundle;

import com.cluit.util.dataTypes.Results;
import com.cluit.util.structures.Pair;
import com.cluit.visual.widget.DataTable;

public class TableTabController extends _AbstractTableTab{	
	@FXML Group group;
	HBox wrap_pane;
	
	private final HashMap<String, Integer> mLabelToRow    = new HashMap<>();
	private final ArrayList< DataTable >   mDataTables    = new ArrayList<>();
	private final ArrayList< DataTable >   mRefDataTables = new ArrayList<>();
	
	private boolean   first = true;
	private DataTable mLabelTable;
	private int 	  mNextLabelIndex = 0; 	
	private int 	  mNextRun = 1;

	@FXML protected void clear_tab(ActionEvent ae){
		group.getChildren().clear();
		wrap_pane = new HBox();
		group.getChildren().add(wrap_pane);
		
		mLabelToRow.clear();
		mNextLabelIndex = 0;
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
		
		//This VBox encloses the entire result group, with the grid and the labels and buttons and such
		VBox vbox = new VBox();
		vbox.setAlignment( Pos.TOP_CENTER );
		
		vbox.getChildren().add( getDataTable( r ) );
		
		//The reference part contains the mean values of reference variables related to a certain cluster
		
				
		//Misc data is the user designated data, assigned from a JS file
		if( r.hasMiscData() )
			vbox.getChildren().addAll( getMiscDataLabels(r) );
		vbox.getChildren().addAll( getSquaredErrors(r) );
		
		//The wrap pane is the basis for the tab.
		//We also keep a collection of all grids, so we can extend it later on if new variables are added (and we need to move the misc data down)
		wrap_pane.getChildren().add(vbox);
	}

	private Node getDataTable(Results r) {
		//The grid part contains the cluster's centoid data, as well as the "X" button
		String[] subH = new String[  r.numberOfClusters() ];
		for(int i = 0; i < subH.length; i++)
			subH[i] = "Cluster " + i;
		
		DataTable dataTable = new DataTable("Run " + mNextRun++, subH);
			
		//Fetch the labels. Inform the data table of the label-row relationships
		String[] labels = r.getLabels();
		dataTable.setLabelRowRelationship( getLabelRowRelationship( labels ) );
		dataTable.setTotalLabelCount( mLabelToRow.size() );
		//For each cluster, we create a list of Label - Data pairs, where the label denotes what feature we are looking at
		//and the Data represents that clusters centoid for the current feature
		for( int col = 0; col < r.numberOfClusters(); col++){
			ArrayList<Pair<String, String>> labelDataPairs = new ArrayList<Pair<String, String>>();		
			double[] clusterCentoid = r.getCentoid(col);
			
			for(int j = 0; j < labels.length; j++){
				Pair<String, String> pair = new Pair<String, String>(labels[j], String.valueOf(clusterCentoid[j]) );
				labelDataPairs.add(pair);
			}
			
			dataTable.setColumnData(col, mLabelToRow.size(), labelDataPairs);
		}
		
		mDataTables.add(dataTable);
		return dataTable;
	}

	private ArrayList<Pair<String, Integer>> getLabelRowRelationship(String[] labels) {
		ArrayList<Pair<String, Integer>> relations = new ArrayList<Pair<String, Integer>>();
		
		for( int i = 0; i < labels.length; i++){
			String  currLabel = labels[i];
			Integer currIndex = mLabelToRow.get(currLabel);
			
			if( currIndex == null ){
				currIndex = addNewDataLabel( currLabel );
			}
			
			Pair<String, Integer> pair = new Pair<String, Integer>( currLabel, currIndex);
			relations.add( pair );
		}
		
		return relations;
	}

	private int addNewDataLabel(String label) {
		//Register a new relationship within this tab
		mLabelToRow.put(label, mNextLabelIndex);
		
		//Paint the new label at the bottom of the label table
		mLabelTable.appendText(label);
		
		//Add empty spaces to all other data tables
		for( DataTable t : mDataTables)
			t.addEmptyRow();
		
		return mNextLabelIndex++;
	}

	//A button that removes all results from a perticular run
	private Button getClearButton(VBox vbox, int numberOfClusters) {
		Button button = new Button("X");
		button.setMaxSize(5, 5);		
		button.setFont( new Font(4) );
		
		//Locate the button in the top right corner of the grid
		GridPane.setConstraints(button, numberOfClusters - 1, 0, 1, 1, HPos.RIGHT, VPos.TOP, Priority.NEVER, Priority.NEVER);
		
		button.setOnAction( (ae) -> vbox.getChildren().clear() );
		
		return button;
	}

	/**Creates the basic layout, with the left label grid and the similar
	 * 
	 */
	private void createBaseLayout() {
		mLabelTable = new DataTable(" ", " ");	//We want the extra spacing that having a heading gives, to keep in line with the data labels
		wrap_pane.getChildren().add(mLabelTable);
		
		wrap_pane.setMinWidth(1);
		wrap_pane.getChildren().add( new Separator(Orientation.VERTICAL) );
		
		first = false;
	}
	
	/**Creates labels with the squared error sums for the different clusters, as well as creates one total squared error, which is the sum
	 * of the squared error from the different clusters
	 * 
	 * @param r
	 * @return
	 */
	private ArrayList<Label> getSquaredErrors(Results r) {
		ArrayList<Label> errorLabels = new ArrayList<>();
		double[] errors = r.getSquaredErrors();
		double totalErrors = 0;
		errorLabels.add(new Label(" ") ); //Adds an empty row
		
		//Calculate the number of digits in the largest error.
		//This'll be used to place the numbers in the labels correctly
		double largestNr = 0;
		for(double d : errors){
			if( Math.abs(d) > largestNr )
				largestNr = Math.abs(d);
		}
		int offset = (int) (Math.log10(largestNr) + 6); 
		
		for(int i = 0; i < errors.length; i++){
			Label label = new Label( String.format("SSE Clu"+i+": %"+offset+".4f", errors[i]) );
			DataTable.skinDataLabel(label);
			
			totalErrors += errors[i];
			
			errorLabels.add(label);
		}
		
		Label blank = new Label(" ");
		Label total = new Label( "Total SSE" );
		Label val   = new Label( String.format("%.4f", totalErrors) );
		
		total.setStyle("-fx-underline: true;");
		DataTable.skinDataLabel(blank);
		DataTable.skinDataLabel(total);
		DataTable.skinDataLabel(val);

		errorLabels.add(blank); errorLabels.add(total); errorLabels.add(val);
		return errorLabels;
	}
	
	private ArrayList<Label> getMiscDataLabels(Results r) {
		ArrayList<Label> miscDataLabels = new ArrayList<>();
		ArrayList<Pair<String, Double>> miscData = r.getMiscData();
		
		miscDataLabels.add(new Label(" ") ); //Adds an empty row
		
		for( int i = 0; i < miscData.size(); i++){
			Pair<String, Double> data = miscData.get(i);
			
			Label label = new Label(data.left +": " + data.right);
			DataTable.skinDataLabel(label);
			
			miscDataLabels.add(label);
		}
		
		return miscDataLabels;
	}

	/**************************************************************************************************************************/
	/**************************************************************************************************************************/
	/**************************************************************************************************************************/
	
	

	static int idx = 1;
	@Override
	protected void DEBUG_paintThisTab(int i) {
		if( first )
			createBaseLayout();
		addNewDataLabel("HEJ");
	}
}
