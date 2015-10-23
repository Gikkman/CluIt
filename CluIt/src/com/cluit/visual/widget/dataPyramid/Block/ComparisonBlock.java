package com.cluit.visual.widget.dataPyramid.Block;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.cluit.util.Const;
import com.cluit.util.structures.TypedObservableObjectWrapper;

/**This class represents a certain type of DataBlock that visualizes differences between two different blocks
 * 
 * @author Simon
 *
 */
public class ComparisonBlock extends DataBlock {
	
	private final Rectangle positionLargerRect, positionOverlapRect, deadRect, rangeOverlapRect, rangeLargerRect;
	private final HBox positionBox = new HBox(), rangeBox = new HBox();
	private final double positionOverlap, positionDelta, rangeOverlap, rangeDelta;

	public ComparisonBlock(TypedObservableObjectWrapper<Color> overlapColor,
						   TypedObservableObjectWrapper<Color> targetLargerColor,
						   TypedObservableObjectWrapper<Color> sourceLargerColor,
						   double targetsPos, double targetsRange, double sourcesPos, double sourcesRange) {
		super(overlapColor);
		
		this.positionOverlap = Math.min(targetsPos, sourcesPos);
		this.rangeOverlap    = Math.min(targetsRange, sourcesRange);
		this.positionDelta = Math.abs(targetsPos - sourcesPos);
		this.rangeDelta    = Math.abs(targetsRange - sourcesRange); 
		
		this.positionLargerRect = new Rectangle( 100 , Const.BLOCK_DEFAULT_HEIGHT);
		this.positionOverlapRect= new Rectangle( 100 , Const.BLOCK_DEFAULT_HEIGHT);
		this.rangeOverlapRect	= new Rectangle( 100 , Const.BLOCK_DEFAULT_HEIGHT);
		this.rangeLargerRect	= new Rectangle( 100 , Const.BLOCK_DEFAULT_HEIGHT);	
		this.deadRect  = new Rectangle( 0.0, Const.BLOCK_DEFAULT_HEIGHT);		
		
		add(positionBox, 0, 0);
		add(deadRect,    1, 0);
		add(rangeBox,    2, 0);
		
		positionBox.setAlignment(Pos.CENTER_RIGHT);
		positionBox.getChildren().addAll( positionLargerRect, positionOverlapRect);
		rangeBox.getChildren().addAll( rangeOverlapRect, rangeLargerRect);
		
		deadRect.setFill( Color.TRANSPARENT );
		initColor(positionOverlapRect, overlapColor);
		initColor(rangeOverlapRect,    overlapColor);
		initColor(positionLargerRect,  targetsPos > sourcesPos ? targetLargerColor : sourceLargerColor);
		initColor(rangeLargerRect,     targetsRange > sourcesRange ? targetLargerColor : sourceLargerColor);
	}

	@Override
	public void bindWidth(NumberExpression deadZone, NumberExpression maxWidth) {
		super.bindWidth(deadZone, maxWidth);
		
		positionLargerRect.widthProperty().bind( maxWidth.multiply( 0.5 ).multiply(positionDelta));
		positionOverlapRect.widthProperty().bind( maxWidth.multiply( 0.5 ).multiply(positionOverlap));
		deadRect.widthProperty().bind(deadZone);
		rangeOverlapRect.widthProperty().bind( maxWidth.multiply( 0.5 ).multiply(rangeOverlap));
		rangeLargerRect.widthProperty().bind(  maxWidth.multiply( 0.5 ).multiply(rangeDelta));

		ColumnConstraints posCol = new ColumnConstraints(); 
		ColumnConstraints deadCol = new ColumnConstraints(); 
		ColumnConstraints rangeCol= new ColumnConstraints(); 
		
		  posCol.setHalignment(HPos.RIGHT);	
		 deadCol.setHalignment(HPos.CENTER);	
		rangeCol.setHalignment(HPos.LEFT);	
		
		  posCol.minWidthProperty().bind(maxWidth.multiply(0.5));   
		 deadCol.minWidthProperty().bind(deadZone);                          
		rangeCol.minWidthProperty().bind(maxWidth.multiply(0.5));  
		
		getColumnConstraints().addAll(posCol, deadCol, rangeCol);
	}

	@Override
	public void bindHeight(NumberExpression height) {
		super.bindHeight(height);
		
		positionLargerRect.heightProperty().bind(height);
		positionOverlapRect.heightProperty().bind(height);
		deadRect.heightProperty().bind(height);
		rangeOverlapRect.heightProperty().bind(height);
		rangeLargerRect.heightProperty().bind(height);

	}
	
	private void initColor(Rectangle rect, TypedObservableObjectWrapper<Color> color){
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(Const.COMPARISON_BLOCK_STROKE_WIDTH);
		rect.setFill(color.getValue());
		
		color.addPropertyChangeListener( (event) -> rect.setFill( color.getValue()));
	}
}
