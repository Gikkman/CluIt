package com.cluit.visual.widget.dataPyramid;

import javafx.beans.binding.NumberExpression;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.VBox;

import java.util.ArrayList;

import com.cluit.visual.widget.dataPyramid.Block.Block;
import com.cluit.visual.widget.dataPyramid.Block.Block.BlockType;

public class PyramidRow extends VBox {
	private static final int SPACING = 5;
	
	private final HBox					  row = new HBox();
	private final ComboBox<RowAction> comboBox = new ComboBox<>();
	private final Button 				  reloadButton = new Button("R");
	private final ArrayList<Pyramid> 	  pyramids = new ArrayList<>();
	
	//*******************************************************************************************************
	//region							PUBLIC 			
	//*******************************************************************************************************

	public PyramidRow(String rowName){
		setAlignment(Pos.CENTER_LEFT);
		row.setAlignment(Pos.CENTER_LEFT);
		
		insertRowHeader(rowName);	
		insertVerticalSeparator();
		
		comboBox.setVisibleRowCount(5);
		comboBox.valueProperty().addListener( (o, oldV, newV) -> { 
			if( oldV != null )
				oldV.onDeselect(pyramids);
			if( newV != null )
				newV.onSelect(pyramids); 
		} );
		
		reloadButton.setOnAction( (event) -> {
			if( comboBox.getValue() != null )
				comboBox.getValue().onSelect(pyramids);
		} );	
		
		getChildren().add(row);
		getChildren().add( new Separator() );
	}
	

	public void addPyramid( Pyramid ... pyramids){
		for( Pyramid p : pyramids ){
			this.pyramids.add(p);
		}
		
		row.getChildren().addAll(pyramids);
	}
	
	
	public void addPyramidAction( RowAction ... actions ){
		if( actions.length > 0 ){
			comboBox.getItems().addAll(actions);
			comboBox.setValue( comboBox.getItems().get(0) );
		}
		
	}
	
	public void setBlockBindings( NumberExpression deadZone, NumberExpression maxWidth, NumberExpression height ){
		for( Pyramid p : pyramids ){
			p.setBlockWidthBinding(deadZone, maxWidth);
			p.setBlockHeightBinding(height);
		}
			
	}
	
	public void setBlockDisplay( Block.BlockType displayMode ){
		for( Pyramid p : pyramids ){
			p.setBlockDisplay( displayMode );
		}
	}
	
	
	//endregion *********************************************************************************************
	//		
	//region							PRIVATE 		
	private void insertRowHeader(String rowName) {
		Pane master = this;
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
		
		headerBox.add(reloadButton, 1, 1);
		
		//The "switch block mode" button
		Button mode = new Button("Pos Only");
		mode.setPrefWidth(80);
		mode.setOnAction( new EventHandler<ActionEvent>() {
			boolean posOnly = true;
			@Override
			public void handle(ActionEvent event) {
				if( posOnly ){
					for( Pyramid p : pyramids )
						p.setBlockDisplay( BlockType.Range );
					mode.textProperty().set( "Pos | Range");
					posOnly = false;
				}
				else if( !posOnly ){
					for( Pyramid p : pyramids )
						p.setBlockDisplay( BlockType.Mean );
					mode.textProperty().set( "Pos Only");
					posOnly = true;
				}
			}
		});
		GridPane.setHalignment(mode, HPos.CENTER);
		headerBox.add(mode, 0, 2);
		
		//The "Delete row" button
		Button button = new Button("Delete run");
		button.setOnAction( (ev) -> ( (Pane) master.getParent() ).getChildren().remove(master) );
		GridPane.setValignment(button, VPos.BOTTOM);
		GridPane.setHalignment(button, HPos.LEFT);
		GridPane.setVgrow(button, Priority.ALWAYS);
		headerBox.add(button, 0, 3);
		
		row.getChildren().add(headerBox);		
	}
	
	private void insertVerticalSeparator() {
		Separator s = new Separator(Orientation.VERTICAL);
		s.setPadding( new Insets(SPACING) );
		row.getChildren().add( s ); 
	}
	//*******************************************************************************************************	
	//endregion *********************************************************************************************
	//*******************************************************************************************************
}
