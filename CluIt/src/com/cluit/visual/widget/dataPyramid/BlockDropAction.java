package com.cluit.visual.widget.dataPyramid;

import javafx.scene.layout.Pane;

import com.cluit.visual.widget.dataPyramid.Block.Block;

public interface BlockDropAction {
	public void onDrop(Pane parent, Block source, Block target);
}
