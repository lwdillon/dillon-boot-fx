package com.lw.fx.client.view.system.notice;

import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysDictData;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.HTMLEditor;

import java.net.URL;
import java.util.ResourceBundle;

public class NoticeInfoView implements FxmlView<NoticeInfoViewModel>, Initializable {

    @InjectViewModel
    private NoticeInfoViewModel noticeInfoViewModel;

    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private ToggleGroup group;

    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private HTMLEditor noticeContentHtmlEd;
    @FXML
    private TextField noticeTitleField;

    @FXML
    private ComboBox<SysDictData> noticeTypeCombo;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        noticeTitleField.textProperty().bindBidirectional(noticeInfoViewModel.noticeTitleProperty());
        noticeTypeCombo.valueProperty().bindBidirectional(noticeInfoViewModel.noticeTypeProperty());
        noticeTypeCombo.setItems(noticeInfoViewModel.getNoticeDataObservableList());
        noticeInfoViewModel.noticeContentProperty().addListener((observable, oldValue, newValue) -> {
            noticeContentHtmlEd.setHtmlText(newValue);
        });
        noticeInfoViewModel.subscribe("commitHtmlText", (key, payload) -> {
            noticeInfoViewModel.noticeContentProperty().setValue(noticeContentHtmlEd.getHtmlText());
        });
        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("0");
        deactivateRadioBut.setUserData("1");
        noticeInfoViewModel.statusProperty().setValue("0");

        initListeners();
    }

    private void initListeners() {

        noticeInfoViewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                noticeInfoViewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });

    }

    public void saveHtmlText() {
        noticeInfoViewModel.noticeContentProperty().setValue(noticeContentHtmlEd.getHtmlText());
    }

}
