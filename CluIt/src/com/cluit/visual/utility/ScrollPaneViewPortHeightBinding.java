package com.cluit.visual.utility;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.ScrollPane;

public class ScrollPaneViewPortHeightBinding extends DoubleBinding {

    private final ScrollPane root;

    public ScrollPaneViewPortHeightBinding(ScrollPane root) {
        this.root = root;
        super.bind(root.viewportBoundsProperty());
    }

    @Override
    protected double computeValue() {
        return root.getViewportBounds().getHeight();
    }
}
