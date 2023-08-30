package com.lw.fx.client.view.system.dict.type;

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

public class DictTypeInfoView implements FxmlView<DictTypeInfoViewModel>, Initializable {

    @InjectViewModel
    private DictTypeInfoViewModel dictTypeInfoViewModel;


    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private TextField dictNameTextField;

    @FXML
    private TextField dictTypeTextField;

    @FXML
    private ToggleGroup group;


    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private TextArea remarksTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        dictNameTextField.textProperty().bindBidirectional(dictTypeInfoViewModel.dictNameProperty());
        dictTypeTextField.textProperty().bindBidirectional(dictTypeInfoViewModel.dictTypeProperty());
        remarksTextArea.textProperty().bindBidirectional(dictTypeInfoViewModel.remarkProperty());


        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("0");
        deactivateRadioBut.setUserData("1");
        dictTypeInfoViewModel.statusProperty().setValue("0");

        initListeners();
    }

    private void initListeners() {

        dictTypeInfoViewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                dictTypeInfoViewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });

    }

}
