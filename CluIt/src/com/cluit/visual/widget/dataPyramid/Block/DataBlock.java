package com.cluit.visual.widget.dataPyramid.Block;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.cluit.util.Const;
import com.cluit.util.structures.TypedObservableObjectWrapper;

abstract class DataBlock extends GridPane{

	private final TypedObservableObjectWrapper<Color> color;
	
	DataBlock(TypedObservableObjectWrapper<Color> color){
		this.color = color;
		this.setAlignment( Pos.CENTER );
		this.setStyle( "-fx-background-color: WHITE;");
	}
	
	abstract void bindWidth( NumberExpression deadZone, NumberExpression maxWidth);
	abstract void bindHeight(NumberExpression height);
	
	
	protected void initColors(Rectangle ... rects){
		for(Rectangle rect : rects){
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(Const.BLOCK_STROKE_WIDTH);
			rect.setFill(color.getValue());
			
			this.color.addPropertyChangeListener( (event) -> rect.setFill( color.getValue()));
		}
	}
}
