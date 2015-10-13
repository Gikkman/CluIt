package com.cluit.visual.utility;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.ScrollPane;

public class ScrollPaneViewPortWidthBinding extends DoubleBinding {

    private final ScrollPane root;

    public ScrollPaneViewPortWidthBinding(ScrollPane root) {
        this.root = root;
        super.bind(root.viewportBoundsProperty());
    }

    @Override
    protected double computeValue() {
        return root.getViewportBounds().getWidth();
    }
}
