package com.lw.fx.client.view.window;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class WindowViewModel implements ViewModel {

    private BooleanProperty mainViewVisble = new SimpleBooleanProperty();

    public boolean isMainViewVisble() {
        return mainViewVisble.get();
    }

    public BooleanProperty mainViewVisbleProperty() {
        return mainViewVisble;
    }

}
