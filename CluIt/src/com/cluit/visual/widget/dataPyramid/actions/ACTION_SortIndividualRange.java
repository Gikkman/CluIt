package com.cluit.visual.widget.dataPyramid.actions;

import java.util.ArrayList;

import com.cluit.util.structures.KeyPriorityQueue_Min;
import com.cluit.visual.widget.dataPyramid.Pyramid;
import com.cluit.visual.widget.dataPyramid.RowAction;
import com.cluit.visual.widget.dataPyramid.Block.Block;

public class ACTION_SortIndividualRange extends RowAction{

	@Override
	public void onSelect(ArrayList<Pyramid> pyramids) {
		for( Pyramid p : pyramids){
			Block[]  blocks = p.getBlocks();
			
			KeyPriorityQueue_Min<Integer> rangeQueue = new KeyPriorityQueue_Min<>();
			for( int i = 0; i < blocks.length; i++)
				rangeQueue.add( blocks[i].getRange(), i);
			
			int[] newOrder = new int[ blocks.length ];
			for( int i = 0; i < blocks.length; i++)
				newOrder[i] = rangeQueue.poll();
			
			p.setBlockOrder(newOrder);
		}
	}

	@Override
	public String toString(){
		return "Sort by individual range";
	}
	
}
