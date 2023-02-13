package org.dillon.fx.view.system;

import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class DeptManageViewModel implements ViewModel, SceneLifecycle {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private StringProperty text = new SimpleStringProperty();


    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }

    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT,(s, objects) -> {});
    }
}
