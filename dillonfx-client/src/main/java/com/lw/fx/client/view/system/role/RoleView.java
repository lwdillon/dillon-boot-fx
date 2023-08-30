package com.lw.fx.client.view.system.role;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.util.NodeUtils;
import com.lw.fx.client.view.control.PagingControl;
import com.lw.fx.client.view.control.WFXGenericDialog;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Tweaks.*;

public class RoleView implements FxmlView<RoleViewModel>, Initializable {

    @InjectViewModel
    private RoleViewModel roleViewModel;

    @FXML
    private VBox contentPane;
    @FXML
    private Button addBut;

    @FXML
    private TableColumn<SysRole, Date> createTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button editBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<SysRole, String> optCol;


    @FXML
    private Button resetBut;

    @FXML
    private TableColumn<?, ?> roleIdCol;

    @FXML
    private TableColumn<?, ?> roleKeyCol;

    @FXML
    private TableColumn<?, ?> roleNameCol;

    @FXML
    private TableColumn<?, ?> roleSortCol;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysRole, Boolean> selCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<SysRole, Boolean> statusCol;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysRole> tableView;

    @FXML
    private TextField roleSearchField;


    private RingProgressIndicator loading;

    private WFXGenericDialog dialog;


    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(roleViewModel.totalProperty());
        roleViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        roleViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        roleViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            roleViewModel.queryRoleList();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            roleViewModel.queryRoleList();
        });
        loading = new RingProgressIndicator();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        roleSearchField.textProperty().bindBidirectional(roleViewModel.roleNameProperty());
        statusCombo.valueProperty().bindBidirectional(roleViewModel.statusProperty());
        startDatePicker.valueProperty().bindBidirectional(roleViewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(roleViewModel.endDateProperty());
        searchBut.setOnAction(event -> roleViewModel.queryRoleList());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> roleViewModel.reset());
        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showRoleInfoDialog(tableView.getSelectionModel().getSelectedItem().getRoleId());
        });
        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            roleViewModel.getSysRoles().forEach(role -> {
                if (role.isSelect()) {
                    delIds.add(role.getRoleId());
                }
            });
            showDelDialog(delIds);
        });
        statusCombo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(ObjectUtil.equal("0", item) ? "正常" : ObjectUtil.equal("1", item) ? "停用" : "全部");
                        }
                    }
                };
            }
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);
        roleIdCol.setCellValueFactory(new PropertyValueFactory<>("roleId"));
        roleNameCol.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        roleKeyCol.setCellValueFactory(new PropertyValueFactory<>("roleKey"));
        roleSortCol.setCellValueFactory(new PropertyValueFactory<>("roleSort"));

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
                        ToggleSwitch state = new ToggleSwitch();
                        state.setSelected(item);
                        setGraphic(state);
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

                        Button editBut = new Button("修改");
                        editBut.setOnAction(event -> showRoleInfoDialog(getTableRow().getItem().getRoleId()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(CollUtil.newArrayList(getTableRow().getItem().getRoleId())));
                        remBut.setGraphic(FontIcon.of(Feather.TRASH));
                        remBut.getStyleClass().addAll(FLAT, ACCENT);

                        MenuItem resetPwdItem = new MenuItem("数据权限");
                        resetPwdItem.setOnAction(event -> showAuthDataDialog(getTableRow().getItem().getRoleId()));
                        MenuItem assignRolesItme = new MenuItem("分配用户");
                        assignRolesItme.setOnAction(event -> {
                            ViewTuple<AuthUserView, AuthUserViewModel> load = FluentViewLoader.fxmlView(AuthUserView.class).load();
                            load.getViewModel().setRoleId(getTableRow().getItem().getRoleId());
                            load.getViewModel().allocatedList();
                            MvvmFX.getNotificationCenter().publish("addTab", "分配用户", "", load.getView());
                        });
                        MenuButton moreBut = new MenuButton("更多");
                        moreBut.getItems().addAll(resetPwdItem, assignRolesItme);
                        moreBut.getStyleClass().addAll(FLAT, ACCENT);
                        HBox box = new HBox(editBut, remBut, moreBut);
                        box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                        setGraphic(box);
                    }
                }
            };
        });


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
        tableView.setItems(roleViewModel.getSysRoles());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        addBut.setOnAction(event -> showRoleInfoDialog(null));

    }

    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();
        }
        return dialog;
    }


    private void showRoleInfoDialog(Long userId) {

        ViewTuple<RoleInfoView, RoleInfoViewModel> load = FluentViewLoader.fxmlView(RoleInfoView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().updateSysRoleInfo(userId);
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(userId))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    roleViewModel.queryRoleList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(userId) ? "编辑角色" : "添加角色");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());
    }

    private void showAuthDataDialog(Long userId) {

        ViewTuple<AuthDataView, AuthDataViewModel> load = FluentViewLoader.fxmlView(AuthDataView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().updateSysRoleInfo(userId);
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save()).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    roleViewModel.queryRoleList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("分配数据权限");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());
    }

    private void showDelDialog(List<Long> roleIds) {

        if (CollUtil.isEmpty(roleIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> roleViewModel.del(CollUtil.join(roleIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                roleViewModel.queryRoleList();
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + roleIds + "的角色吗？"));
        getDialogContent().show(rootPane.getScene());
    }


}
