package org.dillon.fx.view.system;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import de.saxsys.mvvmfx.utils.notifications.WeakNotificationObserver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class DeptManageView implements FxmlView<DeptManageViewModel>, Initializable {


    @InjectViewModel
    private DeptManageViewModel viewModel;

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        MvvmFX.getNotificationCenter().subscribe("someNotification", new WeakNotificationObserver((key, payload) -> {
            System.err.println(DeptManageView.this.label);
            viewModel.setText(payload[0] + "");
            viewModel.someAction();
        }));
        label.textProperty().bind(viewModel.textProperty());
        viewModel.subscribe(DeptManageViewModel.OPEN_ALERT, (key, payload) -> {
            String message = (String) payload[0];
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(message);
            alert.setContentText(message);
            alert.show();
        });
    }
}
