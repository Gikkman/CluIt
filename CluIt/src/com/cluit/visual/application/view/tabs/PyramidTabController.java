package com.cluit.visual.application.view.tabs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import com.cluit.util.AoP.VariableSingleton;
import com.cluit.util.dataTypes.Results;
import com.cluit.util.methods.ClusteringUtils;
import com.cluit.util.methods.MiscUtils;
import com.cluit.util.structures.TypedObservableObjectWrapper;
import com.cluit.visual.utility.GroupHeightBinding_Local;
import com.cluit.visual.utility.GroupWidthBinding_Local;
import com.cluit.visual.utility.ScrollPaneViewPortHeightBinding;
import com.cluit.visual.utility.ScrollPaneViewPortWidthBinding;
import com.cluit.visual.widget.dataPyramid.Pyramid;
import com.cluit.visual.widget.dataPyramid.PyramidRow;
import com.cluit.visual.widget.dataPyramid.RowAction;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_DragDropOverride;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_SortIndividualRange;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_SortIndividualWeight;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_SortInsertionOrder;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_SortMeanRange;
import com.cluit.visual.widget.dataPyramid.Actions.ACTION_SortMeanWeight;
import com.cluit.visual.widget.dataPyramid.Block.Block;

public class PyramidTabController extends _AbstractTableTab{
	private static final int SPACE_BETWEEN_ROWS = 30, SPACE_BETWEEN_COLOR_PICKERS = 10, SPACE_BETWEEN_PYRAMIDS = 10;
	
	@FXML ScrollPane scroll_pane;
	@FXML AnchorPane anchor_pane;
	@FXML Button button_clear;
	@FXML Group  group_wrapper;
	@FXML protected void clear_tab(ActionEvent ae){ clear(); }	
	
	private VBox vertical_layout;
	private VBox pyramids_layout;
	private HBox color_picker_layout;
	private Slider dead_zone, max_width, height;
	
	private Map< String, TypedObservableObjectWrapper<Color> > feature_to_color = new HashMap<>();
	private ArrayList< TypedObservableObjectWrapper<int[]> > order_list = new ArrayList<>();
	private int run = 1;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		
		createBaseLayout();
	}

	private void createBaseLayout() {
		//Set up the main vertical layout
		vertical_layout = new VBox(SPACE_BETWEEN_ROWS);
		group_wrapper.getChildren().add(vertical_layout);
		
		//Create the width and height sliders
		AnchorPane slidersAnchor = new AnchorPane();
		slidersAnchor.setMinHeight(25);
		Pane sliderBox = createSliderBox();
		AnchorPane.setTopAnchor(sliderBox, 5.0);
		AnchorPane.setLeftAnchor(sliderBox, 5.0);
		slidersAnchor.getChildren().add(sliderBox);
		vertical_layout.getChildren().add(sliderBox);
		
		//Create the color-pickers row at the top
		AnchorPane colorsAnchor = new AnchorPane();
		colorsAnchor.setMinHeight(25);
		color_picker_layout = new HBox(SPACE_BETWEEN_COLOR_PICKERS);
		color_picker_layout.setAlignment(Pos.CENTER_LEFT);
		AnchorPane.setTopAnchor(color_picker_layout, 5.0);
		AnchorPane.setLeftAnchor(color_picker_layout, 5.0);
		colorsAnchor.getChildren().add(color_picker_layout);
		vertical_layout.getChildren().add(colorsAnchor);
		
		//Creates the vbox into which the different pyramid rows'll go
		pyramids_layout = new VBox( SPACE_BETWEEN_PYRAMIDS );
		vertical_layout.getChildren().add(pyramids_layout);
			
		GroupHeightBinding_Local groupH = new GroupHeightBinding_Local(group_wrapper);
		GroupWidthBinding_Local  groupW = new GroupWidthBinding_Local(group_wrapper);
		ScrollPaneViewPortHeightBinding scrollH = new ScrollPaneViewPortHeightBinding(scroll_pane);
		ScrollPaneViewPortWidthBinding  scrollW = new ScrollPaneViewPortWidthBinding(scroll_pane);
		
		button_clear.layoutYProperty().bind( scroll_pane.vvalueProperty()
				.multiply( groupH
						.subtract( scrollH))
				.add( scrollH.subtract(30) ));
		button_clear.layoutXProperty().bind( scroll_pane.hvalueProperty()
				.multiply( groupW
						.subtract( scrollW))
				.add( 10 ));
	}	
	
	private void clear(){
		feature_to_color.clear();
		color_picker_layout.getChildren().clear();
		order_list.clear();
		pyramids_layout.getChildren().clear();
	}
	
	@Override
	protected void paintNewResults(Results results){
		int numberClusters = results.getNumberOfClusters();
		int numberFeatures = results.getNumberOfFeatures();
		
		//Create the block order object which this Results' pyramids will listen to
		int[] internal_block_order = new int[numberFeatures];
		for( int i = 0; i < numberFeatures; i++)
			internal_block_order[i] = i;
		TypedObservableObjectWrapper<int[]> blockOrder = new TypedObservableObjectWrapper<int[]>(internal_block_order);	
		order_list.add(blockOrder);
		
		//Creates the pyramids
		PyramidRow row = new PyramidRow("Run " + run++); //Will contain the pyramids
		for( int i = 0; i < numberClusters; i++){	//Each cluster is represented by one pyramid
			Pyramid pyramid = new Pyramid( VariableSingleton.getInstance().getClusterName(i) , blockOrder);
			pyramid.setAlignment(Pos.CENTER);
			
			double[][] blockValues = ClusteringUtils.transposeMatrix( results.getNormalizedClusterEntryValues(i) );
			for( int j = 0; j < numberFeatures; j++){		
				String feature = results.getLabels()[j];
				Block block = new Block( feature,  getColor( feature ), blockValues[j] );
				pyramid.add(block);
			}			

			//Add the reference data (strings displayed below the pyramid)
			if( results.hasReferenceData() ){
				String[] refLabels = results.getReferenceLabels();
				double[] refData = results.getReferenceDataMeans()[i];
				
				for( int j = 0; j < refLabels.length; j++)
					pyramid.addMiscData( String.format( refLabels[j] + " : %.4f", refData[j]) );
			}
			row.addPyramid(pyramid);
		}		
		
		row.addPyramidAction( getPyramidActions() );
		row.setBlockBindings(dead_zone.valueProperty(), max_width.valueProperty(), height.valueProperty());
		//Add it to the view
		pyramids_layout.getChildren().add(row);
	}

	private RowAction[] getPyramidActions() {
		RowAction[] actions = new RowAction[6];
		actions[0] = new ACTION_SortInsertionOrder();
		actions[1] = new ACTION_SortMeanWeight();
		actions[2] = new ACTION_SortIndividualWeight();
		actions[3] = new ACTION_SortMeanRange();
		actions[4] = new ACTION_SortIndividualRange();
		actions[5] = new ACTION_DragDropOverride();
		return actions;
	}
	
	private TypedObservableObjectWrapper<Color> getColor(String feature){
		if( !feature_to_color.containsKey(feature) )
			addNewColorPicker(feature);
		return feature_to_color.get(feature);
	}
	
	 private Pane createSliderBox() {
	    	HBox sliderLayout = new HBox( 10 );
	    	sliderLayout.setAlignment( Pos.CENTER_LEFT );
	    	height = getSlider("H");
			dead_zone = getSlider("MinW");
			max_width = getSlider("MaxW");
			sliderLayout.getChildren().addAll( new Label("Dead zone: "), dead_zone, new Label("Max Width: "), max_width,  new Label("Height: "), height);
			return sliderLayout;
	    }

		private Slider getSlider(String type) {
			Slider slider = new Slider();
			slider.setShowTickLabels(true);
			slider.setShowTickMarks(true);
			//There is a lot of hard coded values here. But that is to avoid cluttering down the CONST section
			if( type.matches("MinW") ){
				slider.setMin(0);
				slider.setMax(100);
				slider.setValue(20);
				slider.setMajorTickUnit(50);
				slider.setMinorTickCount(10);
				slider.setBlockIncrement(5);
			}
			else if( type.matches("MaxW") ){
				slider.setMin(0);
				slider.setMax(800);
				slider.setValue(300);
				slider.setMajorTickUnit(200);
				slider.setMinorTickCount(50);
				slider.setBlockIncrement(10);
			} 
			else if ( type.matches("H") ){
				slider.setMin(0);
				slider.setMax(300);
				slider.setValue(100);
				slider.setMajorTickUnit(50);
				slider.setMinorTickCount(10);
				slider.setBlockIncrement(5);
			} else {
				System.err.println("Unknow slider type " + MiscUtils.getStackPos());
			}
			
			slider.valueProperty().addListener( (o, oldV, newV) ->  { slider.valueProperty().set( (newV.intValue() / 2)*2 ); } );
			
			return slider;	
		}
	
	private void addNewColorPicker(String feature) {
		TypedObservableObjectWrapper<Color> color = VariableSingleton.getInstance().getFeatureColor(feature);		
		ColorPicker cp = new ColorPicker( color.getValue() );
		cp.setOnAction( (event) -> {
			color.setValue( cp.getValue() );
		});
		feature_to_color.put(feature, color);
		
		color_picker_layout.getChildren().addAll( new Label(feature), cp);
	}

	@Override
	protected void DEBUG_paintThisTab(int i) {
				
		TypedObservableObjectWrapper<int[]> block_order = new TypedObservableObjectWrapper<int[]>( new int[] {0, 1, 2, 3} );
		
		PyramidRow row = new PyramidRow("Run"); //Will contain the pyramids
		for( int nrPyr = 0; nrPyr < 3; nrPyr++){
			Pyramid pyramid = new Pyramid( VariableSingleton.getInstance().getClusterName(i), block_order);
			for( int nrBlock = 0; nrBlock < 4; nrBlock++){	
				Block block;
				if( nrPyr == 1 && nrBlock == 1)
					block = new Block( "Block " + nrBlock,  getColor( "Block " + nrBlock ), new double[] {1.0} );
				else if (nrPyr == 1 && nrBlock == 2)
					block = new Block( "Block " + nrBlock,  getColor( "Block " + nrBlock ), new double[] {1.0, 0.0} );
				else
					block = new Block( "Block " + nrBlock,  getColor( "Block " + nrBlock ), new double[] {(1.0 - nrBlock/10 - 0.05) / (nrBlock+1), 0.8, 0.6, 0.4} );
				pyramid.add(block);
			}
			
			pyramid.addMiscData( "Hej: Där");
			row.addPyramid(pyramid);
		}		
		
		row.setBlockBindings(dead_zone.valueProperty(), max_width.valueProperty(), height.valueProperty());
		row.addPyramidAction( getPyramidActions() );
		//Add it to the view
		pyramids_layout.getChildren().add(row);
	}
}
