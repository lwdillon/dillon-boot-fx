package org.dillon.fx.view.system;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleManageView implements FxmlView<RoleManageViewModel>, Initializable {

    @InjectViewModel
    private RoleManageViewModel viewModel;

    @FXML
    private Label label;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        label.textProperty().bind(viewModel.textProperty());
        MvvmFX.getNotificationCenter().subscribe("someNotification", (key, payload) -> {
            viewModel.setText(payload[0] + "");
        });
    }
}
