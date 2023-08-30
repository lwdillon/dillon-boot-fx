package com.lw.fx.client.view.system.user;

import cn.hutool.core.date.DateUtil;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.util.NodeUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Tweaks.*;

/**
 * 身份验证角色视图
 *
 * @author wenli
 * @date 2023/05/17
 */
public class AuthRoleView implements FxmlView<AuthRoleViewModel>, Initializable {

    @InjectViewModel
    private AuthRoleViewModel authRoleViewModel;

    @FXML
    private CheckBox selectAllCheckBox;
    @FXML
    private TableView<SysRole> tableView;

    @FXML
    private TableColumn<SysRole, Date> createTimeCol;

    @FXML
    private TextField nickNameTextField;

    @FXML
    private TableColumn<?, ?> roleIdCol;

    @FXML
    private TableColumn<?, ?> roleKeyCol;

    @FXML
    private TableColumn<?, ?> roleNameCol;

    @FXML
    private TableColumn serialNumbCol;

    @FXML
    private TextField userNameTextField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        nickNameTextField.textProperty().bind(authRoleViewModel.nickNameProperty());
        userNameTextField.textProperty().bind(authRoleViewModel.userNameProperty());
        selectAllCheckBox.setOnAction(event -> authRoleViewModel.selectAll(selectAllCheckBox.isSelected()));

        serialNumbCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        serialNumbCol.setCellFactory(CheckBoxTableCell.forTableColumn(serialNumbCol));
        serialNumbCol.setEditable(true);
        roleIdCol.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleKeyCol.setCellValueFactory(new PropertyValueFactory<>("roleKey"));
        roleNameCol.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setCellFactory(new Callback<TableColumn<SysRole, Date>, TableCell<SysRole, Date>>() {
            @Override
            public TableCell<SysRole, Date> call(TableColumn<SysRole, Date> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Date item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                this.setText(DateUtil.format(item, "yyyy-MM-dd HH:mm:ss"));
                            }
                        }

                    }
                };
            }
        });


        tableView.setItems(authRoleViewModel.getRoles());
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }
    }


}
