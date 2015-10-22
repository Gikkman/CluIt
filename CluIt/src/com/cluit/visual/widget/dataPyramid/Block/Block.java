package com.cluit.visual.widget.dataPyramid.Block;

import javafx.beans.binding.NumberExpression;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.HashMap;

import com.cluit.util.Const;
import com.cluit.util.methods.MiscUtils;
import com.cluit.util.structures.TypedObservableObjectWrapper;

/**This class represents a block in a data visualization pyramid. The block is responsible for all visualization related to the data that 
 * corresponds to this blocks feature.
 * 
 * @author Simon
 *
 */
public class Block extends StackPane{
	public enum BlockType {Mean, Range};
	
	private final HBox  rectBox = new HBox();
	private final Label	name;
	
	private final HashMap<BlockType, DataBlock> blockSet = new HashMap<>();
	private final double position, range;
	private double[] values;
	
	/**Creates a block that is intended to be added to a pyramid.
	 * 
	 * @param name Name of this block
	 * @param color If the observable value is updated, the block will change color accordingly
	 * @param position Width of the block, in relation to the maximum width. 1.0 is the maximum weight, and 0.0 the minimum
	 */
	public Block( String name, TypedObservableObjectWrapper<Color> color, double position){
		this.name = new Label(" " + name + " ");
		
		this.position = position;
		this.range = -1;
		
		this.blockSet.put( BlockType.Mean, new MeanBlock(color, this.position) );
		
		init();
	}

	/**Creates a block that is intended to be added to a pyramid. 
	 * This constructor is intended to be fed all cluster members values for a given feature
	 * 
	 * @param name Name of this block
	 * @param color If the observable value is updated, the block will change color accordingly
	 * @param values A collection of normalized doubles. They should all be in the range [0 - 1] (inclusive).
	 */
	public Block( String name, TypedObservableObjectWrapper<Color> color, double[] values){
		if( values == null || values.length == 0 )
			System.err.println("Error in Block : Constructor. Invalid 'values' parameter " + MiscUtils.getStackPos());
		
		//Calculate position and range!
		double pos = 0, min = Double.POSITIVE_INFINITY, max = Double.NEGATIVE_INFINITY;
		for( double value : values ){
			pos += value;
			min = min > value ? value : min;
			max = max < value ? value : max;
		}
		pos /= values.length;
		
		this.values = values;
		this.name = new Label(" " + name + " ");
		this.position = pos;
		this.range = max - min;
		
		this.blockSet.put( BlockType.Mean, new MeanBlock(color, position) );
		this.blockSet.put( BlockType.Range, new RangeBlock(color, position, range) );
		
		init();
	}
	
	public Block(String name, DataBlock dataBlock) {
		this.name = new Label(" " + name +" ");
		
		position = -1; 
		range = -1;
		values = null;
		
		this.prefWidth(Const.BLOCK_DEFAULT_WIDTH);
		this.name.setStyle( "-fx-background-color: LIGHTGREY;");
		
		rectBox.setAlignment( Pos.TOP_CENTER );
		rectBox.getChildren().add( dataBlock );
		
		this.getChildren().addAll(rectBox, this.name);
	}

	public void bindWidht( NumberExpression deadZone, NumberExpression maxWidth){
		for( DataBlock b : blockSet.values() )
			b.bindWidth(deadZone, maxWidth);
	}
	
	public void bindHeight( NumberExpression height){
		for( DataBlock b : blockSet.values() )
			b.bindHeight(height);
	}
	
	public void changeDisplayMode(BlockType displayMode){
		if( blockSet.containsKey(displayMode) ){
			rectBox.getChildren().clear();
			rectBox.getChildren().add( blockSet.get(displayMode) );
		} else {
			System.out.println("No block with that type present " + MiscUtils.getStackPos() );
		}
	}

	public TypedObservableObjectWrapper<Color> getColor(){
		return blockSet.values().iterator().next().getColor();
	}
	
	public NumberExpression getDeadZoneExpression(){
		return blockSet.values().iterator().next().getDeadZoneExpression();
	}
	
	public NumberExpression getMaxWidthExpression(){
		return blockSet.values().iterator().next().getMaxWidthExpression();
	}
	
	public NumberExpression getHeightExpression(){
		return blockSet.values().iterator().next().getHeightExpression();
	}
	
	public double getPosition(){
		return position;
	}
	
	public double getRange() {
		return range;
	}
	
	public String getName(){
		return name.getText();
	}
	
	/**Sets up the name and colorisation of the block.
	 */
	private void init() {
		this.prefWidth(Const.BLOCK_DEFAULT_WIDTH);
		this.name.setStyle( "-fx-background-color: LIGHTGREY;");
		
		rectBox.setAlignment( Pos.TOP_CENTER );
		rectBox.getChildren().add( blockSet.get( BlockType.Mean ) );		
		getChildren().addAll(rectBox, name);
	}
	
	@Override
	public String toString(){
		return name.getText();
	}

}
