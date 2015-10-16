package com.cluit.visual.widget.dataPyramid;

import java.util.ArrayList;

import com.cluit.visual.widget.dataPyramid.Pyramid.BlockOrdering;

public class ACTION_SortInsertionOrder extends RowAction{

	@Override
		public void onSelect(ArrayList<Pyramid> pyramids) {
			if( pyramids.size() > 0 ){
				
				int[] blockOrder = new int[pyramids.get(0).getNumberOfBlocks()];
				for( int i = 0; i < blockOrder.length; i++)
					blockOrder[i] = i;
				pyramids.get(0).setBlockOrder_WithForce(blockOrder);
				
				for(int i = 1; i < pyramids.size(); i++)
					pyramids.get(i).setBlockOrderMode( BlockOrdering.LINKED );
			}
		}
		
		@Override
		public String toString(){
			return "Insertion ordering";
		}
}
