package com.cluit.visual.widget.dataPyramid.Block;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.cluit.util.Const;
import com.cluit.util.structures.TypedObservableObjectWrapper;

class MeanBlock extends DataBlock {

	private final Rectangle rect;
	private final double mean;
	
	MeanBlock(TypedObservableObjectWrapper<Color> color, double mean) {
		super(color);
				
		this.mean = mean;
		this.rect = new Rectangle(Const.BLOCK_DEFAULT_WIDTH*mean, Const.BLOCK_DEFAULT_HEIGHT);
		initColors(rect);
		
		GridPane.setHalignment(rect, HPos.CENTER);
		add(rect, 0, 0);
	}

	@Override
	public void bindWidth(NumberExpression deadZone, NumberExpression maxWidth) {
		super.bindWidth(deadZone, maxWidth);
		
		rect.widthProperty().bind( deadZone.add( maxWidth.multiply(mean)));	
		
		ColumnConstraints c = new ColumnConstraints();
		c.minWidthProperty().bind( maxWidth.add(deadZone));
		getColumnConstraints().add(c);
	}
	
	@Override
	void bindHeight(NumberExpression height) {
		super.bindHeight(height);
		
		rect.heightProperty().bind(height);		
	}
}
