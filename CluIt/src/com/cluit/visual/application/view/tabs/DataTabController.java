package com.cluit.visual.application.view.tabs;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.ArrayList;

import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Data;
import com.cluit.util.dataTypes.Results;

public class DataTabController implements Initializable{

	@FXML TableView<Double[]> table;
	
	final ObservableList<Double[]> rows = FXCollections.observableArrayList();
	
	@Override
	public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
			
		table.setEditable(false);
		table.setId("data-table");
		table.setItems(rows);
				
		VariableSingleton.getInstance().setDataListener( (evt) -> newDataLoaded( (Data) evt.getNewValue() ) ); 	
	};
	
	private void newDataLoaded(Data data){
		clearTab();
		paintTab(data);
	}
	
	private void paintTab(Data data){
		//TODO: Add reference data
		table.getColumns().addAll( getDataColumns( data.getLabels() ) );
		
		
		if( data.hasReferenceData() ){			
			table.getColumns().add( getEmptyColumn() );
			
			int idx = data.getLabels().length+1; //+1 to compensate for the empty separator column
			table.getColumns().addAll( getReferenceColumns( idx, data.getReferenceLabels() ) );
			
			rows.addAll( boxDoubles( data.getData(), data.getReferenceData() ) );
		} else {
			rows.addAll( boxDoubles( data.getData() ) );
		}		
	}

	private ArrayList<TableColumn<Double[], String>> getDataColumns( String[] labels) {
		return getReferenceColumns(0, labels);
	}
	
	private ArrayList<TableColumn<Double[], String>> getReferenceColumns(int idx, String[] labels) {
		ArrayList<TableColumn<Double[], String>> out = new ArrayList<>();
		
		for( int i = 0; i < labels.length; i++ ){
			out.add( getNewColumn( labels[i], i+idx) );	
		}
		
		return out;
	}

	private ArrayList<Double[]> boxDoubles(double[][] data) {
		ArrayList<Double[]> out = new ArrayList<>();
		
		for( double[] row : data ){
			Double[] boxedRow = new Double[row.length];
			
			for( int i = 0; i < row.length; i++){
				boxedRow[i] = row[i];
			}
			
			out.add(boxedRow);
		}
		
		return out;
	}
	
	private ArrayList<Double[]> boxDoubles(double[][] data, double[][] refData) {
		ArrayList<Double[]> out = new ArrayList<>();
		
		//TODO: Might need to find the longest refData row... This'll assume that the first row's length is equal to the longest one's
		int rowLenght = data[0].length + refData[0].length + 1;	//+1 for the empty separator column. It will be filled with null
		
		for( int row = 0; row < data.length; row++){
			Double[] boxedRow = new Double[ rowLenght ];
			
			for( int i = 0; i < rowLenght; i++){
				int dataCellIdx = i;
				int refDataCellIdx = i - data[row].length - 1; //-1 since we've skipped the empty separator column	
				
				if( i < data[row].length )
					boxedRow[i] = data[row][dataCellIdx];
				else if( i > data[row].length ){
					if( row < refData.length &&  refDataCellIdx < refData[row].length )
						boxedRow[i] = refData[row][ refDataCellIdx ];
				}
			}
			
			out.add(boxedRow);
		}
		
		return out;
	}


	private void clearTab(){
		rows.clear();
		table.getColumns().clear();		
	}
	
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	private TableColumn<Double[], String> getNewColumn(String label, int i) {
		final int index = i;
		
		TableColumn<Double[], String> col = new TableColumn<Double[], String>( label );
		col.setCellValueFactory( new Callback< CellDataFeatures<Double[],String>, ObservableValue<String> >() {
			@Override
			public ObservableValue<String> call(CellDataFeatures<Double[], String> param) {
				Double val = param.getValue().length > index ? param.getValue()[index] : null;
				String string = val != null ? String.format("%.3f", val) : " ";
				ReadOnlyStringWrapper d =  new ReadOnlyStringWrapper( string );
				return d;
			}					
		} );
		
		col.setMinWidth(50);
		col.setSortable(false);
		
		return col;
	}
	
	private TableColumn<Double[], String> getEmptyColumn(){
		TableColumn<Double[], String> col = new TableColumn<Double[], String>(" ");
		
		col.setMinWidth(25);
		col.setSortable(false);
		
		return col;
	}
	
	/************************************************************************/
	/************************************************************************/
	/************************************************************************/
	/************************************************************************/

	private static boolean first = true;
	private static int intenger  = 0;
	protected void DEBUG_paintThisTab(Object[] args) {
		if( args[0] instanceof Results )
			return;		
		if( first ){
			for( int i = 0; i < 15; i++){
								
				table.getColumns().add( getNewColumn("i = " + (i+1), i) );
			}
			
			first = false;
		}
		
		Double[] e = { (double) (1+intenger), (double) (2+intenger), (double) (3+intenger), (double) (4+intenger), (double) (5+intenger),
					   (double) (6+intenger), (double) (7+intenger), (double) (8+intenger), (double) (9+intenger), (double) (10+intenger),
					   (double) (11+intenger), (double) (12+intenger), (double) (13+intenger), (double) (14+intenger), (double) (15+intenger++)   };	
		rows.add(e);
	}
}
