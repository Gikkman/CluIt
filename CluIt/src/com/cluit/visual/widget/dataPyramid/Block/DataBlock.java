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
	
	NumberExpression deadZone, maxWidth, height;
	
	DataBlock(TypedObservableObjectWrapper<Color> color){
		this.color = color;
		this.setAlignment( Pos.CENTER );
		this.setStyle( "-fx-background-color: WHITE;");
		
		this.setGridLinesVisible(true);
	}
	
	void bindWidth( NumberExpression deadZone, NumberExpression maxWidth){
		this.deadZone = deadZone;
		this.maxWidth = maxWidth;
		
		minWidthProperty().bind( maxWidth.add(deadZone) );
	}
	void bindHeight(NumberExpression height){
		this.height = height;
	}
	
	
	protected void initColors(Rectangle ... rects){
		for(Rectangle rect : rects){
			rect.setStroke(Color.BLACK);
			rect.setStrokeWidth(Const.BLOCK_STROKE_WIDTH);
			rect.setFill(color.getValue());
			
			this.color.addPropertyChangeListener( (event) -> rect.setFill( color.getValue()));
		}
	}

	public TypedObservableObjectWrapper<Color> getColor() {
		return color;
	}

	NumberExpression getDeadZoneExpression() { 
		return deadZone;
	}

	public NumberExpression getMaxWidthExpression() {
		return maxWidth;
	}

	public NumberExpression getHeightExpression() {
		return height;
	}
}
