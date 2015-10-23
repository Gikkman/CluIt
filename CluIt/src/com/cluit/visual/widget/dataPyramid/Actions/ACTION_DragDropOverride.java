package com.cluit.visual.widget.dataPyramid.Actions;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;

import com.cluit.util.methods.MiscUtils;
import com.cluit.util.preferences.PreferenceManager;
import com.cluit.util.structures.TypedObservableObjectWrapper;
import com.cluit.visual.widget.dataPyramid.Pyramid;
import com.cluit.visual.widget.dataPyramid.PyramidDropAction;
import com.cluit.visual.widget.dataPyramid.RowAction;
import com.cluit.visual.widget.dataPyramid.Block.Block;
import com.cluit.visual.widget.dataPyramid.Block.ComparisonBlock;

public class ACTION_DragDropOverride extends RowAction {
	private static final String WIN_WIDTH_ID = "ComparisonWindowWidth", WIN_HEIGHT_ID = "ComparisonWindowHeight", DEAD_ZONE_ID = "ComparisonWindowDeadzoneValue",
							    MAX_WIDTH_ID = "ComparisonWindowMaxWidthValue", HEIGHT_ID = "ComparisonWindowHeightValue",
							    OVERLAP_COLOR_ID = "ComparisonOverlapColorID", TARGET_LARGER_COLOR_ID = "ComparisonTargetLargerColorID", SOURCE_LARGER_COLOR_ID = "ComparisonSourceLargerColorID";
						
	private TypedObservableObjectWrapper<Color> targetLargerColor ;
	private TypedObservableObjectWrapper<Color> soruceLargerColor ;
	private TypedObservableObjectWrapper<Color> overlapColor      ;
	private Slider dead_zone, max_width, height;
	private final HashMap<Pyramid, PyramidDropAction> pyramidDropActionCache = new HashMap<>();
	
	public ACTION_DragDropOverride() {
		targetLargerColor= new TypedObservableObjectWrapper<Color>( PreferenceManager.getFeatureColor( TARGET_LARGER_COLOR_ID, Color.BLUE ) );
		soruceLargerColor= new TypedObservableObjectWrapper<Color>( PreferenceManager.getFeatureColor( SOURCE_LARGER_COLOR_ID, Color.RED  ) );
		overlapColor     = new TypedObservableObjectWrapper<Color>( PreferenceManager.getFeatureColor( OVERLAP_COLOR_ID, Color.GREEN ) );
	}
	
	@Override
	public void onSelect(ArrayList<Pyramid> pyramids) {
		/*This here IF-check is in case the user reloads this RowAction.
		*
		* If we didn't do this check and this RowAction was reloaded, the cache would
		* be overwritten with this class' drop action, and the original drop actions
		* (those we cached in the first place) would be lost
		*/
		if( pyramidDropActionCache.size() == 0)
			for( Pyramid p : pyramids ){
				pyramidDropActionCache.put(p, p.getPyramidDropAction() );
				p.setPyramidDropAction( (parent, source, target) -> thisAction(parent, source, target) );
			}
	}
	
	@Override
	public void onDeselect(java.util.ArrayList<Pyramid> pyramids) {
		for( Pyramid p : pyramids ){
			p.setPyramidDropAction( pyramidDropActionCache.get(p) );
		}		
		pyramidDropActionCache.clear();
	};

	@Override
	public String toString() {
		return "Drop action override";
	}
	
	//TODO: Bug with pyramids with different block order
	
	private void thisAction(Pane parent, Pyramid source, Pyramid target){		
		
		//The containing pane
		GridPane box = new GridPane();	
		box.setAlignment(Pos.CENTER);
		box.setVgap(10);
		box.setPadding( new Insets(5) );
		box.add( createColorBox(),  0, 0);
		box.add( createSliderBox(), 0, 1);
		RowConstraints r1 = new RowConstraints(25), r2 = new RowConstraints(25);
		box.getRowConstraints().addAll(r1, r2);
		
		//Create an ordered array, with index i = i. 
		int[] order = new int[ source.getNumberOfBlocks() ];
		for( int i = 0; i < order.length; i++ )
			order[i] = i;
		//Pyramids needs certain parameters encased in TypedObservableObjectWrappers
		TypedObservableObjectWrapper<int[]>  blockOrder = new TypedObservableObjectWrapper<int[]>(order);
		TypedObservableObjectWrapper<String> heading    = new TypedObservableObjectWrapper<String>("Comparison pyramid\n" + "Drag source: " + source.getHeading() + "\t\tDrop target: " + target.getHeading());	
		//The actual comparison pyramid
		Pyramid pyramid = new Pyramid(heading, blockOrder);
		pyramid.setPadding( new Insets(25) );
		pyramid.add( createBlocks(source, target) );		
		GridPane.setConstraints(pyramid, 0, 2, 1, 1, HPos.CENTER, VPos.BOTTOM, Priority.ALWAYS, Priority.ALWAYS);
		box.add( pyramid, 0, 2 );
		
		//Display the pyramid
		Stage stage = new Stage();
		stage.setScene( new Scene(box) );
		stage.setWidth( PreferenceManager.getInteger(WIN_WIDTH_ID,  1280) );
		stage.setHeight(PreferenceManager.getInteger(WIN_HEIGHT_ID, 900));
		stage.setAlwaysOnTop(true);
		stage.show();
		stage.setOnCloseRequest( (ev) -> onCloseWindow( stage ) );
	}

	private void onCloseWindow(Stage stage) {
		PreferenceManager.storeInteger(WIN_WIDTH_ID,  (int) stage.getWidth());
		PreferenceManager.storeInteger(WIN_HEIGHT_ID, (int) stage.getHeight());	
		PreferenceManager.storeInteger(DEAD_ZONE_ID,  (int) dead_zone.getValue() );
		PreferenceManager.storeInteger(MAX_WIDTH_ID,  (int) max_width.getValue() );
		PreferenceManager.storeInteger(HEIGHT_ID,     (int) height.getValue() );
	}

	//Creates the blocks that makes up the pyramid.
	private Block[] createBlocks(Pyramid source, Pyramid target) {
		Block[] blocks = new Block[ source.getNumberOfBlocks() ];
		
		//Since we don't know the order of the blocks currently in any of the two pyramids, we first fetch the order in the target pyramid.
		//We then fetch the blocks from the two pyramids, so we can perform the comparisons.
		int[] targetsOrder 	  = target.getCurrentBlockOrder();
		Block[] targetsBlocks = target.getBlocks();
		Block[] sourcesBlocks = source.getBlocks();
		
		for( int i = 0; i < blocks.length; i++){
			//We make sure to fetch blocks representing the same feature, from both the pyramid's block-collections
			Block targetsCurrentBlock = targetsBlocks[ targetsOrder[i] ];
			Block sorucesCurrentBlock = sourcesBlocks[ targetsOrder[i] ];
			
			String featureName  = targetsCurrentBlock.getName();
			
			double targetsPos 	= targetsCurrentBlock.getPosition();
			double targetsRange = targetsCurrentBlock.getRange();
			double sourcesPos 	= sorucesCurrentBlock.getPosition();
			double sourcesRange = sorucesCurrentBlock.getRange();
			
			//A ComparisonBlock is a block that colors different visuals. The area both pyramid's overlap, given a certain feature,
			//and the segments where one pyramid is larger than the other, are all colored differently.
			ComparisonBlock comparisonBlock = new ComparisonBlock(overlapColor, targetLargerColor, soruceLargerColor, targetsPos, targetsRange, sourcesPos, sourcesRange);
			//Bind the different size-sliders' values to the blocks sizes
			comparisonBlock.bindHeight( height.valueProperty() );
			comparisonBlock.bindWidth(  dead_zone.valueProperty(), max_width.valueProperty() );
			
			blocks[i] = new Block(featureName, comparisonBlock);
		}
		
		return blocks;
	}		
	
	//Creates the three color-pickers, for controlling the colors of the different visuals
	private Pane createColorBox() {
		HBox box = new HBox(10);
		box.setAlignment(Pos.CENTER);
		
		ColorPicker p1 = new ColorPicker( Color.BLUE );
		ColorPicker p2 = new ColorPicker( Color.RED);
		ColorPicker p3 = new ColorPicker( Color.GREEN );
				
		p1.setOnAction( (event) -> targetLargerColor.setValue( p1.getValue()));		
		p2.setOnAction( (event) -> soruceLargerColor.setValue( p2.getValue()));	
		p3.setOnAction( (event) -> overlapColor.setValue( p3.getValue()));	
		
		box.getChildren().addAll( new Label("Drag source larger: "), p2, new Label("Drop target larger: "), p1, new Label("Overlap: "), p3);
		return box;
	}
	
	//Creates the three size-sliders, for controlling the dead zone, height and max width
	private Pane createSliderBox() {
    	HBox sliderLayout = new HBox( 10 );
    	sliderLayout.setAlignment( Pos.CENTER );
    	height = getSlider("H");
		dead_zone = getSlider("DeadZ");
		max_width = getSlider("MaxW");
		sliderLayout.getChildren().addAll( new Label("Dead zone: "), dead_zone, new Label("Max Width: "), max_width,  new Label("Height: "), height);
		return sliderLayout;
    }
	
	private Slider getSlider(String type) {
		Slider slider = new Slider();
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		//There is a lot of hard coded values here. But that is to avoid cluttering down the CONST section.
		if( type.matches("DeadZ") ){
			slider.setMin(0);
			slider.setMax(200);
			slider.setValue( PreferenceManager.getInteger(DEAD_ZONE_ID, 50) );
			slider.setMajorTickUnit(50);
			slider.setMinorTickCount(25);
			slider.setBlockIncrement(5);
		}
		else if( type.matches("MaxW") ){
			slider.setMin(0);
			slider.setMax(3000);
			slider.setValue( PreferenceManager.getInteger(MAX_WIDTH_ID, 600));
			slider.setMajorTickUnit(1000);
			slider.setMinorTickCount(250);
			slider.setBlockIncrement(50);
		} 
		else if ( type.matches("H") ){
			slider.setMin(0);
			slider.setMax(500);
			slider.setValue( PreferenceManager.getInteger(HEIGHT_ID, 200));
			slider.setMajorTickUnit(200);
			slider.setMinorTickCount(25);
			slider.setBlockIncrement(5);
		} else {
			System.err.println("Unknow slider type " + MiscUtils.getStackPos());
		}
		
		return slider;	
	}
}
