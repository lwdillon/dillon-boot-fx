package com.lw.fx.client.view.system.config;

import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigInfoView implements FxmlView<ConfigInfoViewModel>, Initializable {

    @InjectViewModel
    private ConfigInfoViewModel configInfoViewModel;

    @FXML
    private TextField configKeyTextField;

    @FXML
    private TextField configNameTextField;

    @FXML
    private TextField configValueTextField;

    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private ToggleGroup group;

    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private TextArea remarksTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        configKeyTextField.textProperty().bindBidirectional(configInfoViewModel.configKeyProperty());
        configNameTextField.textProperty().bindBidirectional(configInfoViewModel.configNameProperty());
        configValueTextField.textProperty().bindBidirectional(configInfoViewModel.cconfigValueProperty());
        remarksTextArea.textProperty().bindBidirectional(configInfoViewModel.remarkProperty());


        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("Y");
        deactivateRadioBut.setUserData("N");
        configInfoViewModel.configTypeProperty().setValue("Y");

        initListeners();
    }

    private void initListeners() {

        configInfoViewModel.configTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("Y", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                configInfoViewModel.configTypeProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });

    }

}
