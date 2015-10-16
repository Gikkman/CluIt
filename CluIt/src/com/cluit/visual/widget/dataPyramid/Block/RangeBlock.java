package com.cluit.visual.widget.dataPyramid.Block;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.cluit.util.Const;
import com.cluit.util.structures.TypedObservableObjectWrapper;

class RangeBlock extends DataBlock {
	
	private final Rectangle meanRect, rangeRect, deadRect;
	private final double mean, range;

	RangeBlock(TypedObservableObjectWrapper<Color> color, double mean, double range) {
		super(color);

		this.mean = mean;
		this.range = range;
		
		//Set up the rectangles
		this.meanRect  = new Rectangle( Const.BLOCK_DEFAULT_WIDTH * mean * 0.5 , Const.BLOCK_DEFAULT_HEIGHT);
		this.rangeRect = new Rectangle( Const.BLOCK_DEFAULT_WIDTH * range * 0.5, Const.BLOCK_DEFAULT_HEIGHT);
		this.deadRect  = new Rectangle( 0.0									   , Const.BLOCK_DEFAULT_HEIGHT);		
		add(meanRect,  0, 0);
		add(deadRect,  1, 0);
		add(rangeRect, 2, 0);
		
		initColors(meanRect, rangeRect);
		
		deadRect.setFill( Color.TRANSPARENT );
	}

	@Override
	void bindWidth(NumberExpression deadZone, NumberExpression maxWidth) {
		meanRect.widthProperty().bind( maxWidth.multiply( 0.5 ).multiply(mean).subtract(Const.BLOCK_STROKE_WIDTH/2) );
		rangeRect.widthProperty().bind( maxWidth.multiply( 0.5 ).multiply( range ).subtract(Const.BLOCK_STROKE_WIDTH/2) );
		deadRect.widthProperty().bind(deadZone);

		ColumnConstraints meanCol = new ColumnConstraints(); 
		ColumnConstraints deadCol = new ColumnConstraints(); 
		ColumnConstraints rangeCol= new ColumnConstraints(); 
		
		 meanCol.setHalignment(HPos.RIGHT);	
		 deadCol.setHalignment(HPos.CENTER);	
		rangeCol.setHalignment(HPos.LEFT);	
		
		 meanCol.minWidthProperty().bind(maxWidth.multiply(0.5));   
		 deadCol.minWidthProperty().bind(deadZone);                          
		rangeCol.minWidthProperty().bind(maxWidth.multiply(0.5));  
		
		getColumnConstraints().addAll(meanCol, deadCol, rangeCol);
		minWidthProperty().bind( maxWidth.add(deadZone) );
	}

	@Override
	void bindHeight(NumberExpression height) {
		meanRect.heightProperty().bind(height);
		deadRect.heightProperty().bind(height);
		rangeRect.heightProperty().bind(height);

	}
}
