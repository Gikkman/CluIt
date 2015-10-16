package com.cluit.visual.widget.dataPyramid;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;

public class ACTION_DragDropOverride extends RowAction {

	private final HashMap<Pyramid, PyramidDropAction> pyramidDropActionCache = new HashMap<>();
	
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
	
	private void thisAction(Pane parent, Pyramid source, Pyramid target){
		System.out.println( source.getHeading() + " dropped onto " + target.getHeading() );
	}		
}
