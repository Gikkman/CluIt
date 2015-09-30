package com.cluit.visual.application.view.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.cluit.util.dataTypes.Results;
import com.cluit.util.structures.Pair;
import com.cluit.visual.widget.DataTable;

public class TableTabController extends _AbstractTableTab{	
	@FXML Group group;
	
	HBox wrap_pane;
	VBox label_box;
	
	
	private final HashMap<String, Integer> mDataLabelToRow = new HashMap<>();
	private final HashMap<String, Integer> mRefLabelToRow  = new HashMap<>();
	private final ArrayList< DataTable >   mDataTables    = new ArrayList<>();
	private final ArrayList< DataTable >   mRefDataTables = new ArrayList<>();
	
	private boolean   first = true;
	private DataTable mLabelTable;
	private DataTable mRefLabelTable;
	
	private int 	  mNextLabelIndex = 0; 	
	private int 	  mNextRefLabelIndex = 0;
	private int 	  mNextRun = 1;

	@FXML protected void clear_tab(ActionEvent ae){
		group.getChildren().clear();
		
		wrap_pane.getChildren().clear();
		label_box.getChildren().clear();
		
		wrap_pane = new HBox();
		label_box = new VBox();
		
		group.getChildren().add(wrap_pane);
		group.getChildren().add(label_box);
		
		mDataLabelToRow.clear();
		mRefLabelToRow.clear();
		
		mDataTables.clear();
		mRefDataTables.clear();
		
		mNextLabelIndex = 0;
		mNextRefLabelIndex = 0;
		mNextRun = 1;
		
		mLabelTable = null;
		mRefLabelTable = null;
		
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
		HBox hbox = new HBox();
		VBox vbox = new VBox();
		vbox.setAlignment( Pos.TOP_CENTER );
		hbox.getChildren().add(vbox);
		hbox.getChildren().add( new Separator(Orientation.VERTICAL) );
		
		vbox.getChildren().add( getDataTable( r, hbox ) );
		
		//The reference part contains the mean values of reference variables related to a certain cluster
		if( r.hasReferenceData() )
			vbox.getChildren().add( getReferenceTable( r ) );
		
		//Create an empty space, to get some spacing
		vbox.getChildren().add( new Label(" "));
				
		//Misc data is the user designated data, assigned from a JS file
		if( r.hasMiscData() )
			vbox.getChildren().addAll( getMiscDataLabels(r) );
		
		vbox.getChildren().addAll( getSquaredErrors(r) );
		
		//The wrap pane is the basis for the tab.
		//We also keep a collection of all grids, so we can extend it later on if new variables are added (and we need to move the misc data down)
		wrap_pane.getChildren().add(hbox);
		
	}
	
	private Node getDataTable(Results r, Pane pane) {
		//The grid part contains the cluster's centoid data, as well as the "X" button
		String[] subH = new String[  r.numberOfClusters() ];
		for(int i = 0; i < subH.length; i++)
			subH[i] = "Cluster " + i;
		
		DataTable dataTable = new DataTable("Run " + mNextRun++, subH);
			
		//Fetch the labels. Inform the data table of the label-row relationships
		String[] labels = r.getLabels();
		dataTable.setLabelRowRelationship( getLabelRowRelationship( labels ) );
		dataTable.setTotalLabelCount( mDataLabelToRow.size() );
		
		//For each cluster, we create a list of Label - Data pairs, where the label denotes what feature we are looking at
		//and the Data represents that clusters centoid for the current feature
		for( int col = 0; col < r.numberOfClusters(); col++){
			ArrayList<Pair<String, String>> labelDataPairs = new ArrayList<Pair<String, String>>();		
			double[] clusterCentoid = r.getCentoid(col);
			
			for(int j = 0; j < labels.length; j++){
				Pair<String, String> pair = new Pair<String, String>(labels[j], String.valueOf(clusterCentoid[j]) );
				labelDataPairs.add(pair);
			}
			
			dataTable.setColumnData(col, mDataLabelToRow.size(), labelDataPairs);
		}
		
		dataTable.addCloseButton(pane);
		
		mDataTables.add(dataTable);
		return dataTable;
	}
	
	private Node getReferenceTable(Results r) {
		String[] subH = new String[  r.numberOfClusters() ];
		for(int i = 0; i < subH.length; i++)
			subH[i] = "Cluster " + i;
		
		DataTable refTable = new DataTable();
		refTable.addHiddenClusterHeaders(subH);
		
		//Fetch the reference labels
		String[] refLabels = r.getReferenceLabels();
		refTable.setLabelRowRelationship( getReferenceRowRelationship( refLabels ) );
		refTable.setTotalLabelCount( mRefLabelToRow.size() );
		
		//For each cluster, we add the reference data means as labels, and link that data to the corresponding label
		double[][] refMeans = r.getReferenceDataMeans();
		for( int col = 0; col < r.numberOfClusters(); col++){
			ArrayList<Pair<String, String>> pairs = new ArrayList< Pair<String, String>>();
			double[] thisCol = refMeans[col];
			for(int j = 0; j < refLabels.length; j++){
				Pair<String, String> pair = new Pair<String, String>(refLabels[j], String.valueOf(thisCol[j]) );
				pairs.add(pair);
			}
			
			refTable.setColumnData(col, mRefLabelToRow.size(), pairs);
		}
				
		mRefDataTables.add(refTable);
		return refTable;
	}


	private Collection<Pair<String, Integer>> getReferenceRowRelationship(String[] refLabels) {
		ArrayList<Pair<String, Integer>> relations = new ArrayList<Pair<String, Integer>>();
		
		for( int i = 0; i < refLabels.length; i++){
			String  currLabel = refLabels[i];
			Integer currIndex = mRefLabelToRow.get(currLabel);
			
			if( currIndex == null ){
				currIndex = addNewRefLabel( currLabel );
			}
			
			Pair<String, Integer> pair = new Pair<String, Integer>( currLabel, currIndex);
			relations.add( pair );
		}
		
		return relations;
	}

	private ArrayList<Pair<String, Integer>> getLabelRowRelationship(String[] labels) {
		ArrayList<Pair<String, Integer>> relations = new ArrayList<Pair<String, Integer>>();
		
		for( int i = 0; i < labels.length; i++){
			String  currLabel = labels[i];
			Integer currIndex = mDataLabelToRow.get(currLabel);
			
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
		mDataLabelToRow.put(label, mNextLabelIndex);
		
		//Paint the new label at the bottom of the label table
		mLabelTable.appendText(label);
		
		//Add empty spaces to all other data tables
		for( DataTable t : mDataTables)
			t.addEmptyRow();
		
		return mNextLabelIndex++;
	}
	
	private Integer addNewRefLabel(String currLabel) {
		//Register a new relationship within this tab
		mRefLabelToRow.put(currLabel, mNextRefLabelIndex);
		
		//Paint the new label at the bottom of the reference label table
		mRefLabelTable.appendText(currLabel);
		
		//Add empty spaces to all other ref data tables
		for( DataTable t : mRefDataTables)
			t.addEmptyRow();
		
		return mNextRefLabelIndex++;
	}

	/**Creates the basic layout, with the left label grid and the similar
	 * 
	 */
	private void createBaseLayout() {
		label_box = new VBox();
		wrap_pane.getChildren().add(label_box);
		
		mLabelTable = new DataTable(" ", " ");	//We want the extra spacing that having a heading gives, to keep in line with the data labels
		mRefLabelTable = new DataTable();
		mRefLabelTable.addHiddenClusterHeaders(" ");
		
		label_box.getChildren().add(mLabelTable);
		label_box.getChildren().add(mRefLabelTable);
		
		wrap_pane.setMinWidth(1);
		addVerticalSeparator();
		
		first = false;
	}
	
	private void addVerticalSeparator() {
		wrap_pane.getChildren().add( new Separator(Orientation.VERTICAL) );		
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
