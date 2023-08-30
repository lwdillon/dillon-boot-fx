package com.lw.fx.client.view.system.dict.data;

import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class DictDataInfoView implements FxmlView<DictDataInfoViewModel>, Initializable {

    @InjectViewModel
    private DictDataInfoViewModel dictDataInfoViewModel;


    @FXML
    private TextField cssClassTextField;

    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private TextField dictLabelTextField;

    @FXML
    private Spinner<Integer> dictSortSortSpinner;

    @FXML
    private TextField dictTypeTextField;

    @FXML
    private TextField dictValueTextField;

    @FXML
    private ToggleGroup group;

    @FXML
    private ComboBox listClassCombox;

    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private TextArea remarksTextArea;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        cssClassTextField.textProperty().bindBidirectional(dictDataInfoViewModel.remarkProperty());
        dictLabelTextField.textProperty().bindBidirectional(dictDataInfoViewModel.dictLabelProperty());
        dictTypeTextField.textProperty().bindBidirectional(dictDataInfoViewModel.dictTypeProperty());
        dictValueTextField.textProperty().bindBidirectional(dictDataInfoViewModel.dictValueProperty());
        listClassCombox.getSelectionModel().selectedItemProperty();
        remarksTextArea.textProperty().bindBidirectional(dictDataInfoViewModel.remarkProperty());

        dictSortSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));

        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("0");
        deactivateRadioBut.setUserData("1");
        dictDataInfoViewModel.statusProperty().setValue("0");

        initListeners();
    }

    private void initListeners() {
        dictDataInfoViewModel.dictSortProperty().addListener((observable, oldValue, newValue) -> {
            dictSortSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        dictSortSortSpinner.valueProperty().addListener((observable, oldValue, newValue) -> dictDataInfoViewModel.dictSortProperty().setValue(newValue));

        dictDataInfoViewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                dictDataInfoViewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });


    }


}
