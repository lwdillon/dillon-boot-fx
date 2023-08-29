package com.lw.fx.client.view.system.tool;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolView implements FxmlView<ToolViewModel>, Initializable {

    @InjectViewModel
    private ToolViewModel viewModel;
    @FXML
    private WebView webView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewModel.urlProperty().addListener((observable, oldValue, newValue) -> {
            webView.getEngine().load(newValue);
        });
    }
}
