package com.lw.fx.client.view.system.role;

import atlantafx.base.controls.RingProgressIndicator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.domain.SysUserRole;
import com.lw.fx.client.util.NodeUtils;
import com.lw.fx.client.view.control.PagingControl;
import com.lw.fx.client.view.control.WFXGenericDialog;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class AuthUserView implements FxmlView<AuthUserViewModel>, Initializable {

    @InjectViewModel
    private AuthUserViewModel viewModel;

    @FXML
    private CheckBox selAllCheckBox;

    private WFXGenericDialog dialog;
    private RingProgressIndicator loading;
    @FXML
    private StackPane rootPane;

    @FXML
    private TextField userSearchField;
    @FXML
    private TextField phoneField;
    @FXML
    private Button resetBut;
    @FXML
    private Button searchBut;
    @FXML
    private Button addUserBut;
    @FXML
    private Button cancelAuthBut;

    @FXML
    private HBox contentPane;
    @FXML
    private TableView<SysUser> tableView;

    @FXML
    private TableColumn<SysUser, Boolean> selCol;
    @FXML
    private TableColumn<SysUser, String> idCol;
    @FXML
    private TableColumn<SysUser, String> userNameCol;
    @FXML
    private TableColumn<SysUser, String> nickNameCol;
    @FXML
    private TableColumn<SysUser, String> deptCol;
    @FXML
    private TableColumn<SysUser, String> phonenumberCol;
    @FXML
    private TableColumn<SysUser, Boolean> statusCol;
    @FXML
    private TableColumn<SysUser, Date> createTimeCol;
    @FXML
    private TableColumn<SysUser, String> optCol;


    @FXML
    private VBox pagePane;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        pagePane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        viewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.allocatedList();
        });

        loading = new RingProgressIndicator();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);
        searchBut.getStyleClass().addAll(ACCENT);
        searchBut.setOnAction(event -> viewModel.allocatedList());
        userSearchField.textProperty().bindBidirectional(viewModel.userNameProperty());
        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());
        resetBut.setOnAction(event -> viewModel.reset());
        addUserBut.setOnAction(event -> showDialog(viewModel.getRoleId()));
        cancelAuthBut.setOnAction(event -> {
            showCancelAllDialog();
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        nickNameCol.setCellValueFactory(new PropertyValueFactory<>("nickName"));
        deptCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var dept = row.getDept();

            return new SimpleStringProperty(ObjectUtil.isEmpty(dept) ? "" : dept.getDeptName());
        });

        statusCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var item = ObjectUtil.equal("0", row.getStatus());
            return new SimpleBooleanProperty(item);
        });
        statusCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        if (item) {
                            state.setText("正常");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        } else {
                            state.setText("停用");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7, 7, 7, 7));
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });

        optCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button editBut = new Button("取消授权");
                        editBut.setOnAction(event -> showCancelDialog(getTableRow().getItem()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        HBox box = new HBox(editBut);
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });


        phonenumberCol.setCellValueFactory(new PropertyValueFactory<>("phonenumber"));
        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setCellFactory(new Callback<TableColumn<SysUser, Date>, TableCell<SysUser, Date>>() {
            @Override
            public TableCell<SysUser, Date> call(TableColumn<SysUser, Date> param) {
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
        tableView.setItems(viewModel.getUserList());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


    }

    private void showDialog(Long roleId) {
        ViewTuple<AddUserView, AddUserViewModel> load = FluentViewLoader.fxmlView(AddUserView.class).load();
        load.getViewModel().setRoleId(roleId);
        load.getViewModel().unallocatedList();
        getDialogContent().clearActions();

        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {

            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save()).addConsumerInPlatformThread(res -> {

                if (res) {
                    dialog.close();
                    viewModel.allocatedList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("选择用户");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());
    }

    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();
        }
        return dialog;
    }


    private void showCancelDialog(SysUser sysUser) {

        if (ObjectUtil.isEmpty(sysUser)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        SysUserRole sysUserRole = new SysUserRole();
        sysUserRole.setUserId(sysUser.getUserId());
        sysUserRole.setRoleId(viewModel.getRoleId());
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> viewModel.cancel(sysUserRole)).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    viewModel.allocatedList();
                }

            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("确认要取消该用户" + sysUser.getUserName() + "的角色吗？"));
        getDialogContent().show(rootPane.getScene());
    }

    private void showCancelAllDialog() {

        List<Long> userIds = new ArrayList<>();
        viewModel.getUserList().forEach(user -> {
            if (user.isSelect()) {
                userIds.add(user.getUserId());
            }
        });

        if (ObjectUtil.isEmpty(userIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> viewModel.cancelAll(viewModel.getRoleId(), CollUtil.join(userIds, ","))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    viewModel.allocatedList();
                }

            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否取消选中用户授权数据项？"));
        getDialogContent().show(rootPane.getScene());
    }
}
