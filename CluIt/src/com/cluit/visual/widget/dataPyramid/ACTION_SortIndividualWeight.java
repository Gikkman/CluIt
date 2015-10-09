package com.cluit.visual.widget.dataPyramid;

import java.util.ArrayList;

import com.cluit.util.structures.KeyPriorityQueue_Min;

public class ACTION_SortIndividualWeight extends PyramidAction{

	@Override
	public void onSelect(ArrayList<Pyramid> pyramids) {
		for( Pyramid p : pyramids){
			Block[]  blocks = p.getBlocks();
			
			KeyPriorityQueue_Min<Integer> weightQueue = new KeyPriorityQueue_Min<>();
			for( int i = 0; i < blocks.length; i++)
				weightQueue.add( blocks[i].getWeight(), i);
			
			int[] newOrder = new int[ blocks.length ];
			for( int i = 0; i < blocks.length; i++)
				newOrder[i] = weightQueue.poll();
			
			p.setBlockOrder(newOrder);
		}
	}

	@Override
	public String toString(){
		return "Sort by individual weight";
	}
	
}
