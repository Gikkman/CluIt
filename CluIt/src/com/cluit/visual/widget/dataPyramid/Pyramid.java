package com.cluit.visual.widget.dataPyramid;

import javafx.beans.binding.NumberExpression;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.cluit.util.Const;
import com.cluit.util.structures.TypedObservableObjectWrapper;

/**A pyramid is an abstraction of a cluster, where each block in the pyramid visualizes a certain feature. 
 * 
 * @author Simon
 *
 */
public class Pyramid extends VBox{
	public static enum BlockOrdering {LINKED, UNLINKED};
	private static final String UNIQUE_DRAG_KEY = "unique_pyramid_and_block_drag_key";
	
	private final TypedObservableObjectWrapper<int[]> observableBlockOrder;
	private final Map<Integer, Block> id_to_block = new LinkedHashMap<>();
	private final Map<Block, Integer> block_to_id = new LinkedHashMap<>();
	
	private final Label heading;
	private final VBox blockBox = new VBox(Const.PYRAMID_SPACING);
	private final VBox miscBox  = new VBox(Const.PYRAMID_SPACING);
	
	private boolean blockOrderIsLinked = true;
	
	public Pyramid( TypedObservableObjectWrapper<String> name, TypedObservableObjectWrapper<int[]> observableBlockOrder){
		setAlignment(Pos.CENTER);
		setSpacing(Const.PYRAMID_SPACING);
		setPadding( new Insets(Const.PYRAMID_PADDING) );
		setStyle("-fx-background-color: YELLOW;");
		
		blockBox.setAlignment(Pos.TOP_CENTER);
		blockBox.setStyle("-fx-background-color: CYAN;");

		miscBox.setAlignment(Pos.TOP_CENTER);

		this.observableBlockOrder = observableBlockOrder;
		this.observableBlockOrder.addPropertyChangeListener( (event) -> orderChangedEvent(event) );
		
		//Creates the label which specifies the name of the cluster.
		heading = new Label( name.getValue() );
		heading.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
		name.addPropertyChangeListener( (ev) -> heading.textProperty().set( (String) ev.getNewValue() ));
		
		getChildren().add( heading );
		getChildren().add( blockBox );
		getChildren().add( miscBox );
		
		addDragDrop(this, UNIQUE_DRAG_KEY);
	}
	
	public int getNumberOfBlocks(){
		return id_to_block.size();
	}
	
	/**Returns the blocks that make up this pyramid in ID order. <br> That is, the block at index <code>i</code> is known as Block i */
	public final Block[] getBlocks(){
		return id_to_block.values().toArray( new Block[0]);
	}
	
	public void add(Block block){
		id_to_block.put( id_to_block.size(), block);
		block_to_id.put( block, block_to_id.size());
		blockBox.getChildren().add(block);
		
		addDragDrop(block, UNIQUE_DRAG_KEY);
	}
	public void add(Block ... blocks){
		for(Block b : blocks)
			add(b);
	}
	public void add(Collection<? extends Block> blocks){
		for(Block b : blocks)
			add(b);
	}
	
	/**Adds a label bellow the pyramid with the parameter string
	 * @param label
	 */
	public void addMiscData(String label){
		miscBox.getChildren().add( new Label( label ) );
	}
	
	void setBlockWidthBinding(NumberExpression binding){
		for( Block b : block_to_id.keySet() )
			b.bindMaxWidht(binding);
	}
	
	void setBlockHeightBinding(NumberExpression binding){
		for( Block b : block_to_id.keySet() )
			b.bindMaxHeight(binding);
	}
	
	/**Tells the pyramid if it should comfort to the observable block order or not.
	 * When a pyramid is set to LINKED, it will update its block order to match the observable one.
	 * An unlinked pyramid does not order its
	 * blocks after the observable block order, and also doesn't notify other pyramids of its own block order
	 * 
	 * @param mode: LINKED or UNLINKED
	 */
	void setBlockOrderMode( BlockOrdering mode ){
		if( mode == BlockOrdering.LINKED ){
			blockOrderIsLinked = true;
			applyNewBlockOrder( observableBlockOrder.getValue() );
		}
		else
			blockOrderIsLinked = false;
	}
	
	/**Tells a pyramid to order their blocks in a certain way. To get the blocks, call <code>getBlocks()</code>. 
	 * <br><b> This method also unlinks the block order!</b>
	 * <br><br>
	 * Example: If we call <code>getBlocks()</code> and get <code>[B0, B1, B2]</code>, and then pass this method <code>[1, 0, 2]</code>, the order of blocks will
	 * become <code>[B1, B0, B2]</code>.
	 * <br>
	 * @param blockOrder
	 */
	void setBlockOrder(int[] blockOrder){
		setBlockOrderMode( BlockOrdering.UNLINKED );
		applyNewBlockOrder(blockOrder);	
	}
	
	/**Tells a pyramid to order their blocks in a certain way. To get the blocks, call <code>getBlocks()</code>. 
	 * <br><b> This method also links the block order and forces all other linked pyramids to comfort to this order!</b>
	 * <br><br>
	 * Example: If we call <code>getBlocks()</code> and get <code>[B0, B1, B2]</code>, and then pass this method <code>[1, 0, 2]</code>, the order of blocks in all linked pyramids will
	 * become <code>[B1, B0, B2]</code>.
	 * <br>
	 * @param blockOrder
	 */
	public void setBlockOrder_WithForce(int[] blockOrder) {
		observableBlockOrder.setValue(blockOrder);
		setBlockOrderMode( BlockOrdering.LINKED );
	}
	//////////////////////////////////////////////////////////////////////////////
	//region 			PRIVATE METHODS		
	
	private void orderChangedEvent( PropertyChangeEvent event ){
		if(blockOrderIsLinked)
			applyNewBlockOrder( (int[]) event.getNewValue() );
	}
	
	private void applyNewBlockOrder(int[] newOrder) {
		blockBox.getChildren().clear();
       	for(int i : newOrder){
       		blockBox.getChildren().add( id_to_block.get(i) );
       	}
	}

	private void addDragDrop(Node toNode, String dragDropIdentifyer){
		
		//The event which is fired when a drag begins on this block
		toNode.setOnDragDetected( (event) -> {
            Dragboard dragboard = toNode.startDragAndDrop(TransferMode.MOVE);	//Signals that this node can handle drag events. Returns a dragboard
            
            ClipboardContent clipboardContent = new ClipboardContent();		
            clipboardContent.putString(dragDropIdentifyer);
            dragboard.setContent(clipboardContent);		//This puts a string into the dragboard, which we can use to identify the drag event
           
            event.consume();
        } ); 
		
		//The event which is fired when a dragged item begins hovering over another item 
		//Its purpose is to decide if we are allowed to drop the item here or not
		toNode.setOnDragOver( (event) -> {
			Dragboard dragboard = event.getDragboard();
			
			if(dragboard.hasString() 						  && //These checks are to make sure that this is a valid dragDrop action
			   dragboard.getString().equals( dragDropIdentifyer ) ) {
				event.acceptTransferModes( TransferMode.MOVE ); //Sets the "ACCEPT DROP" flag (the cursor indication)
				event.consume();								//Limits event propagation    (good practice)
			}
		} );
		
		//This event is fired when a dragged item is "dropped" onto another object
		//It is fired when an object is dropped onto THIS
		toNode.setOnDragDropped( (event) -> {
			Dragboard dragboard = event.getDragboard();
			boolean dragDropSucceeded = false;
			
            if (dragboard.hasString() ) {	//Not really sure why this is here...
                Pane parent = (Pane) toNode.getParent();
                Object dragSource = event.getGestureSource();
                Object dragTarget = toNode;     
                
                //We check what types the dragged and dropped objects are, to decide how to handle the drag-drop event
                if( dragSource instanceof Pyramid && dragTarget instanceof Pyramid ){
                	changePyramidOrder(parent, dragSource, dragTarget);
                }
                else if( dragSource instanceof Block && dragTarget instanceof Block ){
                	Block sourceBlock = (Block) dragSource;
                	Block targetBlock = (Block) dragTarget;
                	if( sourceBlock.getParent() == targetBlock.getParent() )
                		//If the two blocks have the same parent, the event occurred inside a pyramid. Swap the block's positions
                		changeBlockOrder(parent, dragSource, dragTarget);
                	else
                		//If the two blocks have different parents, the event occurred between pyramids. Swap the pyramid's positions
                		changePyramidOrder((Pane) getPyramid(sourceBlock).getParent(), getPyramid(sourceBlock), getPyramid(targetBlock));
                } 
                else {
                	//In this case, one of the objects involved is a block and the other is a pyramid
                	Block block     = ( dragSource instanceof Block ) ? (Block) dragSource   : (Block) dragTarget;
                	Pyramid pyramid = ( dragSource instanceof Block ) ? (Pyramid) dragTarget : (Pyramid) dragSource; 
                	
                	if( getPyramid(block) == pyramid ){
                		//If the blocks parent equals the pyramid, that means that the drag-drop was performed within the same pyramid. In that case, 
                		//just reorder the blocks in a smart way
                		//TODO: Maybe change order somehow
                	}
                	else {
                		//Otherwise, the drag-drop was between pyramids. In that case, we swap the two pyramid's positions
                		changePyramidOrder((Pane) pyramid.getParent(), getPyramid(block), pyramid);
                	}
                }

                dragDropSucceeded = true;
            }
            event.setDropCompleted(dragDropSucceeded);	//If a drop is completed, the dragboard is frozen to limit more access (good practice)
            event.consume();							//Limits event propagation    (good practice)
		} );
	}
	
	private Pyramid getPyramid(Block block){
		return (Pyramid) block.getParent().getParent();
	}
	
	private int[] getBlockOrder(){
		int[] order = new int[ getNumberOfBlocks() ];
		for( int i = 0; i < order.length; i++)
			order[i] = block_to_id.get( (Block) blockBox.getChildren().get(i) );
		return order;
	}
	
	//endregion //////////////////////////////////////////////////////////////////
	//
	//region 			SWAPPING METHOD		
	private void changePyramidOrder(Pane parent, Object source, Object target){
		int sourceIndex = parent.getChildren().indexOf(source);
       	int targetIndex = parent.getChildren().indexOf(target); 
       	
       	ObservableList<Node> workingCollection = FXCollections.observableArrayList(parent.getChildren());
		Collections.swap(workingCollection, sourceIndex, targetIndex);
		parent.getChildren().setAll(workingCollection);
	}
	
	private void changeBlockOrder(Pane parent, Object source, Object target){
		int sourceIndex = parent.getChildren().indexOf(source);
       	int targetIndex = parent.getChildren().indexOf(target);     
		
       	if( sourceIndex != -1 && targetIndex != -1 &&  observableBlockOrder != null ){
			int[] oldOrder = getBlockOrder();
			int[] newOrder = Arrays.copyOf( oldOrder, oldOrder.length );
			int temp = newOrder[ sourceIndex ];
			
			newOrder[ sourceIndex ] = newOrder[ targetIndex ];
			newOrder[ targetIndex ] = temp;
			
			if( blockOrderIsLinked )
				observableBlockOrder.setValue(newOrder);
			else
				applyNewBlockOrder(newOrder);
		}
	}
	
	//endregion
	//////////////////////////////////////////////////////////////////////////////
}
