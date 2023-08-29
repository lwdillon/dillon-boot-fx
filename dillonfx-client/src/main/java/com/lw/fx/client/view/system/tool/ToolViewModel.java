package com.lw.fx.client.view.system.tool;

import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ToolViewModel implements ViewModel, SceneLifecycle {

    private StringProperty url = new SimpleStringProperty("");

    public ToolViewModel() {
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }

    public String getUrl() {
        return url.get();
    }

    public StringProperty urlProperty() {
        return url;
    }

    public void setUrl(String url) {
        this.url.set(url);
    }
}
