package com.lw.fx.client.view.system.role;

import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.vo.TreeSelect;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class RoleInfoView implements FxmlView<RoleInfoViewModel>, Initializable {

    @InjectViewModel
    private RoleInfoViewModel roleInfoViewModel;

    @FXML
    private RadioButton deactivateRadioBut;

    @FXML
    private CheckBox expansionCheckBox;

    @FXML
    private RadioButton normalRadioBut;

    @FXML
    private TextArea remarksTextArea;

    @FXML
    private TextField roleKeyTextField;

    @FXML
    private TextField roleNameTextField;

    @FXML
    private Spinner<Integer> roleSortSpinner;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private TreeView<TreeSelect> treeView;

    @FXML
    private ToggleGroup group;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        remarksTextArea.textProperty().bindBidirectional(roleInfoViewModel.remarkProperty());
        roleKeyTextField.textProperty().bindBidirectional(roleInfoViewModel.roleKeyProperty());
        roleNameTextField.textProperty().bindBidirectional(roleInfoViewModel.roleNameProperty());
        expansionCheckBox.selectedProperty().bindBidirectional(roleInfoViewModel.expansionAllProperty());
        selectAllCheckBox.selectedProperty().bindBidirectional(roleInfoViewModel.selectAllProperty());
        roleSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));

        normalRadioBut.setSelected(true);
        normalRadioBut.setUserData("0");
        deactivateRadioBut.setUserData("1");
        roleInfoViewModel.statusProperty().setValue("0");
        treeView.rootProperty().bind(roleInfoViewModel.rootItemProperty());
        treeView.setShowRoot(false);
        treeView.setCellFactory(new Callback<TreeView<TreeSelect>, TreeCell<TreeSelect>>() {
            @Override
            public TreeCell<TreeSelect> call(TreeView<TreeSelect> param) {
                return new CheckBoxTreeCell<>() {
                    @Override
                    public void updateItem(TreeSelect item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getLabel());
                        }
                    }
                };
            }
        });
        initListeners();
    }

    private void initListeners() {
        roleInfoViewModel.roleSortProperty().addListener((observable, oldValue, newValue) -> {
            roleSortSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        roleSortSpinner.valueProperty().addListener((observable, oldValue, newValue) -> roleInfoViewModel.roleSortProperty().setValue(newValue));

        roleInfoViewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                normalRadioBut.setSelected(true);
            } else {
                deactivateRadioBut.setSelected(true);
            }
        });
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                roleInfoViewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });
        roleInfoViewModel.expansionAllProperty().addListener((observable, oldValue, newValue) -> {
            treeExpandedAll(treeView.getRoot(), newValue);

        });
        roleInfoViewModel.selectAllProperty().addListener((observable, oldValue, newValue) -> {
            treeSelectAll(treeView.getRoot(), newValue);

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
