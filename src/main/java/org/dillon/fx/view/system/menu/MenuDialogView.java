package org.dillon.fx.view.system.menu;

import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuDialogView implements FxmlView<MenuDialogViewModel>, Initializable {

    @InjectViewModel
    private MenuDialogViewModel viewModel;
    @FXML
    private ComboBox parentIdCombo;

    @FXML
    private RadioButton menuTypeRadio1;
    @FXML
    private RadioButton menuTypeRadio2;
    @FXML
    private RadioButton menuTypeRadio3;

    @FXML
    private TextField iconField;

    @FXML
    private TextField menuNameField;

    @FXML
    private Spinner ordNumFeild;

    @FXML
    private RadioButton frameRadio1;
    @FXML
    private RadioButton frameRadio2;

    @FXML
    private TextField pathField;

    @FXML
    private TextField componentField;

    @FXML
    private TextField permsField;

    @FXML
    private TextField queryField;


    @FXML
    private RadioButton cacheRadio1;

    @FXML
    private RadioButton cacheRadio2;

    @FXML
    private RadioButton visibleRadio1;

    @FXML
    private RadioButton visibleRadio2;

    @FXML
    private RadioButton statusRadio1;

    @FXML
    private RadioButton statusRadio2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        menuTypeRadio1.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("M", viewModel.menuTypeProperty().getValue()), viewModel.menuTypeProperty())
        );
        menuTypeRadio2.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("C", viewModel.menuTypeProperty().getValue()), viewModel.menuTypeProperty())
        );
        menuTypeRadio3.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("F", viewModel.menuTypeProperty().getValue()), viewModel.menuTypeProperty())
        );

        iconField.textProperty().bindBidirectional(viewModel.iconProperty());


        menuNameField.textProperty().bindBidirectional(viewModel.menuNameProperty());


//        ordNumFeild..(viewModel.orderNumProperty());


        frameRadio1.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("0", viewModel.menuTypeProperty().getValue()), viewModel.isFrameProperty())
        );

        pathField.textProperty().bindBidirectional(viewModel.pathProperty());

        componentField.textProperty().bindBidirectional(viewModel.componentProperty());

        permsField.textProperty().bindBidirectional(viewModel.permsProperty());

        queryField.textProperty().bindBidirectional(viewModel.queryProperty());

        cacheRadio1.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("0", viewModel.menuTypeProperty().getValue()), viewModel.isCacheProperty())
        );

        visibleRadio1.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("0", viewModel.menuTypeProperty().getValue()), viewModel.visibleProperty())
        );

        statusRadio1.selectedProperty().bind(Bindings.createBooleanBinding(
                () -> ObjectUtil.contains("0", viewModel.menuTypeProperty().getValue()), viewModel.statusProperty())
        );


    }
}
