package com.cluit.visual.widget.dataPyramid;

import java.util.ArrayList;

public abstract class RowAction {
	public abstract void onSelect( ArrayList<Pyramid> pyramids );
	public void onDeselect( ArrayList<Pyramid> pyramids ) { };
	public abstract String toString();
}
