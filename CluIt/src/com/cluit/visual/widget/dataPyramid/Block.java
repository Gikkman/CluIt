package com.cluit.visual.widget.dataPyramid;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import com.cluit.util.methods.MiscUtils;
import com.cluit.util.structures.TypedObservableObjectWrapper;

/**This class represents a block in a data visualization pyramid. The block is responsible for all visualization related to the data that 
 * corresponds to this blocks feature.
 * 
 * @author Simon
 *
 */
public class Block extends StackPane{	
	private static final double MAX_WIDTH = 100, HEIGHT = 30, STROKE = 3;
	
	private final Rectangle rect;
	private final TypedObservableObjectWrapper<Color> color;
	private final Label		name;
	
	private final double weight;
	private double[] values;
	
	/**Creates a block that is intended to be added to a pyramid.
	 * 
	 * @param name Name of this block
	 * @param color If the observable value is updated, the block will change color accordingly
	 * @param weight Width of the block, in relation to the maximum width. 1.0 is the maximum weight, and 0.0 the minimum
	 */
	public Block( String name, TypedObservableObjectWrapper<Color> color, double weight){
		this.rect = new Rectangle(weight*MAX_WIDTH, HEIGHT);
		this.name = new Label(name);
		this.color = color;
		this.weight = weight;
		
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
		
		double mean = 0;
		for( double d : values )
			mean += d;
		mean /= values.length;
		
		this.values = values;
		this.rect = new Rectangle(mean*MAX_WIDTH, HEIGHT);
		this.name = new Label(name);
		this.color = color;
		this.weight = mean;
		
		init();
	}
		
	public double getWeight(){
		return weight;
	}
	
	public final double[] getValues(){
		return values;
	}

	/**Sets up the name and colorisation of the block.
	 */
	private void init() {
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(STROKE);
		rect.setFill(color.getValue());
		
		this.color.addPropertyChangeListener( (event) -> rect.setFill( color.getValue()));
		visualizeValues();
		
		getChildren().addAll(rect, name);
	}
	
	private void visualizeValues() {
		// TODO Create a way to visualize the values
	}
	
	@Override
	public String toString(){
		return name.getText();
	}
}
