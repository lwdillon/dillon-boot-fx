package com.lw.fx.client.view.system.role;

import cn.hutool.core.util.NumberUtil;
import com.lw.fx.client.domain.vo.TreeSelect;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class AuthDataView implements FxmlView<AuthDataViewModel>, Initializable {

    @InjectViewModel
    private AuthDataViewModel authDataViewModel;

    @FXML
    private CheckBox expansionCheckBox;

    @FXML
    private ComboBox authCombox;

    @FXML
    private VBox authDataPane;

    @FXML
    private TextField roleKeyTextField;

    @FXML
    private TextField roleNameTextField;

    @FXML
    private CheckBox selectAllCheckBox;

    @FXML
    private TreeView<TreeSelect> treeView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        authDataPane.visibleProperty().bind(Bindings.createBooleanBinding(
                () -> authCombox.getSelectionModel().getSelectedIndex() == 1, authCombox.getSelectionModel().selectedIndexProperty()
        ));
        authDataPane.managedProperty().bind(authDataPane.visibleProperty());

        roleKeyTextField.textProperty().bindBidirectional(authDataViewModel.roleKeyProperty());
        roleNameTextField.textProperty().bindBidirectional(authDataViewModel.roleNameProperty());
        expansionCheckBox.selectedProperty().bindBidirectional(authDataViewModel.expansionAllProperty());
        selectAllCheckBox.selectedProperty().bindBidirectional(authDataViewModel.selectAllProperty());
        treeView.rootProperty().bind(authDataViewModel.rootItemProperty());
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

        authCombox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            authDataViewModel.dataScopeProperty().setValue((newValue.intValue() + 1) + "");
        });

        authDataViewModel.dataScopeProperty().addListener((observable, oldValue, newValue) -> {
            authCombox.getSelectionModel().select(NumberUtil.parseInt(newValue) - 1);
        });
        authDataViewModel.expansionAllProperty().addListener((observable, oldValue, newValue) -> {
            treeExpandedAll(treeView.getRoot(), newValue);

        });
        authDataViewModel.selectAllProperty().addListener((observable, oldValue, newValue) -> {
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
