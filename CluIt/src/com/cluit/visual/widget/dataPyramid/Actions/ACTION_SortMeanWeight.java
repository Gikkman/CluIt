package com.cluit.visual.widget.dataPyramid.Actions;

import java.util.ArrayList;

import com.cluit.util.structures.KeyPriorityQueue_Min;
import com.cluit.visual.widget.dataPyramid.Pyramid;
import com.cluit.visual.widget.dataPyramid.RowAction;
import com.cluit.visual.widget.dataPyramid.Block.Block;
import com.cluit.visual.widget.dataPyramid.Pyramid.BlockOrdering;

public class ACTION_SortMeanWeight extends RowAction{

	@Override
	public void onSelect(ArrayList<Pyramid> pyramids) {
		double[] totalWeights = new double[ pyramids.get(0).getNumberOfBlocks() ];	
		KeyPriorityQueue_Min<Integer> queue = new KeyPriorityQueue_Min<>();
		//Calculate sum of weight for each block
		for( Pyramid p : pyramids ){
			Block[] blocks = p.getBlocks();
			for( int i = 0; i < p.getNumberOfBlocks(); i++){
				totalWeights[i] += blocks[i].getPosition();
			}
		}
		//Sort the Weight-Index pairs in ascending order, with respect to the weights
		//
		//I.e, if the weights are now [1, 5, 0.1], they should be ordered [0.1, 1, 5] and we also remember 
		//what block index the weight is calculated from
		for( int i = 0; i < totalWeights.length; i++)
			queue.add( totalWeights[i], i);
		//Extract the new block order (decided from block weights, from smallest to largest)
		int[] newOrder = new int[ totalWeights.length ];
		for( int i = 0; i < totalWeights.length; i++)
			newOrder[i] = queue.poll();
		//Apply the new block order
		pyramids.get(0).setBlockOrder_WithForce(newOrder);
		for(int i = 1; i < pyramids.size(); i++)
			pyramids.get(i).setBlockOrderMode( BlockOrdering.LINKED );
	}
	
	@Override
	public String toString(){
		return "Sort by mean weight";
	}
	
}
