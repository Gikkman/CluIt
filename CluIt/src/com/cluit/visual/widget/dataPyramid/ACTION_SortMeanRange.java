package com.cluit.visual.widget.dataPyramid;

import java.util.ArrayList;

import com.cluit.util.structures.KeyPriorityQueue_Min;
import com.cluit.visual.widget.dataPyramid.Pyramid.BlockOrdering;
import com.cluit.visual.widget.dataPyramid.Block.Block;

public class ACTION_SortMeanRange extends RowAction{

	@Override
	public void onSelect(ArrayList<Pyramid> pyramids) {
		double[] totalRanges = new double[ pyramids.get(0).getNumberOfBlocks() ];	
		KeyPriorityQueue_Min<Integer> queue = new KeyPriorityQueue_Min<>();
		//Calculate sum of weight for each block
		for( Pyramid p : pyramids ){
			Block[] blocks = p.getBlocks();
			for( int i = 0; i < p.getNumberOfBlocks(); i++){
				totalRanges[i] += blocks[i].getRange();
			}
		}
		//Sort the Range-Index pairs in ascending order, with respect to the weights
		//
		//I.e, if the weights are now [1, 5, 0.1], they should be ordered [0.1, 1, 5] and we also remember 
		//what block index the weight is calculated from
		for( int i = 0; i < totalRanges.length; i++)
			queue.add( totalRanges[i], i);
		//Extract the new block order (decided from block weights, from smallest to largest)
		int[] newOrder = new int[ totalRanges.length ];
		for( int i = 0; i < totalRanges.length; i++)
			newOrder[i] = queue.poll();
		//Apply the new block order
		pyramids.get(0).setBlockOrder_WithForce(newOrder);
		for(int i = 1; i < pyramids.size(); i++)
			pyramids.get(i).setBlockOrderMode( BlockOrdering.LINKED );
	}
	
	@Override
	public String toString(){
		return "Sort by mean range";
	}
	
}
