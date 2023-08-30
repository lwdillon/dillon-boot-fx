package com.lw.fx.client.view.system.post;

import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.vo.TreeSelect;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class PostInfoView implements FxmlView<PostInfoViewModel>, Initializable {

    @InjectViewModel
    private PostInfoViewModel postInfoViewModel;

    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private TextArea remarksTextArea;

    @FXML
    private TextField postCodeTextField;

    @FXML
    private TextField postNameTextField;

    @FXML
    private Spinner<Integer> postSortSpinner;

    @FXML
    private ToggleGroup group;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        remarksTextArea.textProperty().bindBidirectional(postInfoViewModel.remarkProperty());
        postCodeTextField.textProperty().bindBidirectional(postInfoViewModel.postCodeProperty());
        postNameTextField.textProperty().bindBidirectional(postInfoViewModel.postNameProperty());
        postSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));

        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("0");
        deactivateRadioBut.setUserData("1");
        postInfoViewModel.statusProperty().setValue("0");

        initListeners();
    }

    private void initListeners() {
        postInfoViewModel.postSortProperty().addListener((observable, oldValue, newValue) -> {
            postSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        postSortSpinner.valueProperty().addListener((observable, oldValue, newValue) -> postInfoViewModel.postSortProperty().setValue(newValue));

        postInfoViewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                postInfoViewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });


    }

    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<TreeSelect> root, boolean expanded) {
        for (TreeItem<TreeSelect> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }

    private void treeSelectAll(TreeItem<TreeSelect> root, boolean select) {
        for (TreeItem<TreeSelect> child : root.getChildren()) {

            if (child instanceof CheckBoxTreeItem<TreeSelect>) {
                ((CheckBoxTreeItem<TreeSelect>) child).setSelected(select);
            }
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, select);
            }
        }
    }

}
