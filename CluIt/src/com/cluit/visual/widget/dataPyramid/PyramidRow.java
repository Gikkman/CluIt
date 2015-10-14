package com.cluit.visual.widget.dataPyramid;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;

public class PyramidRow extends HBox {
	private static final int SPACING = 5;
	
	private final ComboBox<PyramidAction> comboBox = new ComboBox<>();
	private final ArrayList<Pyramid> 	  pyramids = new ArrayList<>();
	
	//*******************************************************************************************************
	//region							PUBLIC 			
	//*******************************************************************************************************

	public PyramidRow(String rowName){
		setAlignment(Pos.CENTER_LEFT);
		
		insertRowHeader(rowName);	
		insertVerticalSeparator();
		
		comboBox.setVisibleRowCount(5);
		comboBox.valueProperty().addListener( (o, oldV, newV) -> { 
			newV.onSelect(pyramids); 
		} );
	}
	

	public void addPyramid( Pyramid ... pyramids){
		for( Pyramid p : pyramids ){
			this.pyramids.add(p);
		}
		
		getChildren().addAll(pyramids);
	}
	
	
	public void addPyramidAction( PyramidAction ... actions ){
		if( actions.length > 0 ){
			comboBox.getItems().addAll(actions);
			comboBox.setValue( comboBox.getItems().get(0) );
		}
		
	}
	
	public void setBlockBindings( NumberExpression width, NumberExpression height ){
		for( Pyramid p : pyramids ){
			p.setBlockWidthBinding(width);
			p.setBlockHeightBinding(height);
		}
			
	}
	
	//endregion *********************************************************************************************
	//		
	//region							PRIVATE 		
	private void insertRowHeader(String rowName) {
		HBox master = this;
		GridPane headerBox = new GridPane();
		headerBox.setVgap(SPACING);
		
		Label title = new Label( rowName );
		GridPane.setHalignment(title, HPos.CENTER);		
		headerBox.add(title, 0, 0);
		
		//This little work-around is to prevent a slight shift in the combo box's position when it gets clicked on (internal JFX bug)
		AnchorPane tempPane = new AnchorPane();
		GridPane.setHalignment(tempPane, HPos.CENTER);
		AnchorPane.setLeftAnchor(comboBox, 2.0);
		tempPane.getChildren().add(comboBox);
		headerBox.add(tempPane, 0, 1);
		
		//The "Delete row" button
		Button button = new Button("Delete run");
		button.setOnAction( (ev) -> ( (Pane) master.getParent() ).getChildren().remove(master) );
		GridPane.setValignment(button, VPos.BOTTOM);
		GridPane.setHalignment(button, HPos.LEFT);
		GridPane.setVgrow(button, Priority.ALWAYS);
		headerBox.add(button, 0, 2);
		
		getChildren().add(headerBox);		
	}
	
	private void insertVerticalSeparator() {
		Separator s = new Separator(Orientation.VERTICAL);
		s.setPadding( new Insets(SPACING) );
		getChildren().add( s ); 
	}
	//*******************************************************************************************************	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
