package com.lw.fx.client.view.system.operlog;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class OperLogInfoView implements FxmlView<OperLogInfoViewModel>, Initializable {

    @InjectViewModel
    private OperLogInfoViewModel viewModel;


    @FXML
    private TextField costTimeField;

    @FXML
    private TextArea errorMsgArea;

    @FXML
    private TextField operNameField;
    @FXML
    private TextField operIpField;

    @FXML
    private TextArea jsonResultArea;

    @FXML
    private TextField methodField;

    @FXML
    private TextArea operParamArea;

    @FXML
    private TextField operTimeField;

    @FXML
    private TextField operUrlField;

    @FXML
    private TextField requestMethodFeild;

    @FXML
    private TextField statusField;

    @FXML
    private TextField titleField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        operTimeField.textProperty().bind(Bindings.createStringBinding(
                () -> DateUtil.format(viewModel.operTimeProperty().get(), "yyyy-MM-dd HH:mm:ss"),
                viewModel.operTimeProperty()));
        errorMsgArea.textProperty().bind(viewModel.errorMsgProperty());
        operNameField.textProperty().bind(viewModel.operNameProperty());
        operIpField.textProperty().bind(viewModel.operIpProperty());
        jsonResultArea.textProperty().bind(viewModel.jsonResultProperty());
        methodField.textProperty().bind(viewModel.methodProperty());
        operParamArea.textProperty().bind(viewModel.operParamProperty());
        costTimeField.textProperty().bind(Bindings.createStringBinding(
                () -> viewModel.costTimeProperty().getValue() + "毫秒",
                viewModel.costTimeProperty()));
        operUrlField.textProperty().bind(viewModel.operUrlProperty());
        requestMethodFeild.textProperty().bind(viewModel.requestMethodProperty());
        statusField.textProperty().bind(Bindings.createStringBinding(
                () -> ObjectUtil.equals(viewModel.statusProperty().getValue(), 0) ? "正常" : "失败",
                viewModel.statusProperty()));
        titleField.textProperty().bind(viewModel.titleProperty());


        initListeners();
    }

    private void initListeners() {


    }


}
