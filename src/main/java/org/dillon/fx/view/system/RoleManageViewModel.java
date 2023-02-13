package org.dillon.fx.view.system;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class RoleManageViewModel implements ViewModel {

    private StringProperty text = new SimpleStringProperty();



    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }
}
