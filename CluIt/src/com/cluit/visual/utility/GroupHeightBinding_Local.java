package com.cluit.visual.utility;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.Group;

public class GroupHeightBinding_Local extends DoubleBinding {

    private final Group root;

    public GroupHeightBinding_Local(Group root) {
        this.root = root;
        super.bind( root.boundsInLocalProperty() );
    }

    @Override
    protected double computeValue() {
        return root.getBoundsInLocal().getHeight();
    }
}
