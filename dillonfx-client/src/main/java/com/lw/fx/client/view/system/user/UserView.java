package com.lw.fx.client.view.system.user;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.util.PasswordTextFormatter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.domain.vo.TreeSelect;
import com.lw.fx.client.util.NodeUtils;
import com.lw.fx.client.view.control.FilterableTreeItem;
import com.lw.fx.client.view.control.PagingControl;
import com.lw.fx.client.view.control.TreeItemPredicate;
import com.lw.fx.client.view.control.WFXGenericDialog;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
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

public class UserView implements FxmlView<UserViewModel>, Initializable {

    @InjectViewModel
    private UserViewModel viewModel;

    @FXML
    private CheckBox selAllCheckBox;

    private WFXGenericDialog dialog;
    private RingProgressIndicator loading;
    @FXML
    private StackPane rootPane;
    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField userSearchField;
    @FXML
    private Button resetBut;
    @FXML
    private Button searchBut;
    @FXML
    private Button addBut;
    @FXML
    private Button editBut;
    @FXML
    private Button delBut;


    @FXML
    private HBox contentPane;
    @FXML
    private TreeView<TreeSelect> treeView;

    @FXML
    private TextField detpSearchField;

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
    private TreeItem<TreeSelect> deptTreeRoot;

    @FXML
    private VBox rightPane;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        pagingControl = new PagingControl();
        rightPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.userList();
        });

        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.userList();
        });
        loading = new RingProgressIndicator();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);
        treeView.setShowRoot(false);

        userSearchField.textProperty().bindBidirectional(viewModel.userNameProperty());
        statusCombo.valueProperty().bindBidirectional(viewModel.statusProperty());
        startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        searchBut.setOnAction(event -> initData());
        resetBut.setOnAction(event -> viewModel.reset());
        searchBut.getStyleClass().addAll(ACCENT);

        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showEditDialog(tableView.getSelectionModel().getSelectedItem().getUserId());
        });
        delBut.setOnAction(event -> {
            Set<Long> delIds = new HashSet<>();
            viewModel.getUserList().forEach(sysUser -> {
                if (sysUser.isSelect()) {
                    delIds.add(sysUser.getUserId());
                }
            });

            if (delIds.isEmpty()) {
                MvvmFX.getNotificationCenter().publish("message", 500, "没有要删除的记录");
                return;
            }
            showDelDialog(CollUtil.join(delIds, ","));
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
        treeView.setCellFactory(new Callback<TreeView<TreeSelect>, TreeCell<TreeSelect>>() {
            @Override
            public TreeCell<TreeSelect> call(TreeView<TreeSelect> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(TreeSelect item, boolean empty) {
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
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            viewModel.deptIdProperty().setValue(newValue.getValue().getId());
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
                        editBut.setOnAction(event -> showEditDialog(getTableRow().getItem().getUserId()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(getTableRow().getItem().getUserId() + ""));
                        remBut.setGraphic(FontIcon.of(Feather.TRASH));
                        remBut.getStyleClass().addAll(FLAT, ACCENT);

                        MenuItem resetPwdItem = new MenuItem("重置密码");
                        resetPwdItem.setOnAction(event -> showResetPwdDialog(getTableRow().getItem()));
                        MenuItem assignRolesItme = new MenuItem("分配角色");
                        assignRolesItme.setOnAction(event -> showAuthRoleDialog(getTableRow().getItem()));
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


        initListener();
        initData();
    }

    private void initListener() {
        selAllCheckBox.setOnAction(event -> {
            viewModel.selectAll(selAllCheckBox.isSelected());
        });
        addBut.setOnAction(event -> showEditDialog(null));
    }

    private FilterableTreeItem<TreeSelect> createDeptTreeRoot() {
        FilterableTreeItem<TreeSelect> rootNode = new FilterableTreeItem<TreeSelect>(new TreeSelect());
        rootNode.setExpanded(true);
        viewModel.getDeptTreeList().forEach(obj -> {
            var child = new FilterableTreeItem<TreeSelect>(obj);
            var childObj = obj.getChildren();
            if (childObj != null) {
                generateTree(child, childObj);
            }
            child.setExpanded(true);
            rootNode.getInternalChildren().add(child);

        });

        rootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            if (detpSearchField.getText() == null || detpSearchField.getText().isEmpty()) {
                return null;
            }
            return TreeItemPredicate.create(actor -> {
                if (containsString(actor.getLabel(), detpSearchField.getText())) {

                    return true;
                } else {
                    return false;
                }
            });
        }, detpSearchField.textProperty()));

        return rootNode;
    }

    private void generateTree(FilterableTreeItem<TreeSelect> parent, List<TreeSelect> children) {
        children.forEach(obj -> {

            var child = new FilterableTreeItem<TreeSelect>(obj);
            var childObj = obj.getChildren();
            if (childObj != null) {
                generateTree(child, childObj);
            }
            child.setExpanded(true);
            parent.getInternalChildren().add(child);

        });
    }

    private boolean containsString(String s1, String s2) {

        return PinyinUtil.getFirstLetter(s1, "").toLowerCase(Locale.ROOT).contains(PinyinUtil.getFirstLetter(s2, "").toLowerCase(Locale.ROOT));
    }

    private void showDelDialog(String userId) {
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            remove(userId);
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + userId + "的用户吗？"));
        getDialogContent().show(rootPane.getScene());
    }


    /**
     * 显示编辑对话框
     */
    private void showEditDialog(Long userId) {


        ViewTuple<UserInfoView, UserInfoViewModel> load = FluentViewLoader.fxmlView(UserInfoView.class).load();
        load.getViewModel().getDeptTreeList().setAll(viewModel.getDeptTreeList());
        load.getViewModel().updateInfo(userId);
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(userId))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    initData();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(userId) ? "编辑用户" : "添加用户");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());


    }


    /**
     * 显示复位pwd对话框
     *
     * @param sysUser 系统用户
     */
    private void showResetPwdDialog(SysUser sysUser) {

        var passwordField = new CustomTextField("");

        var passwordFormatter = PasswordTextFormatter.create(passwordField, '●');
        passwordField.setTextFormatter(passwordFormatter);

        var icon = new FontIcon(Feather.EYE_OFF);
        icon.setCursor(Cursor.HAND);
        icon.setOnMouseClicked(e -> {
            if (passwordFormatter.revealPasswordProperty().get()) {
                passwordFormatter.revealPasswordProperty().set(false);
                icon.setIconCode(Feather.EYE_OFF);
            } else {
                passwordFormatter.revealPasswordProperty().set(true);
                icon.setIconCode(Feather.EYE);
            }
        });
        passwordField.setRight(icon);

        VBox vBox = new VBox(new Label("请输入\"" + sysUser.getUserName() + "\"的新密码"), passwordField);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        SysUser newSysUser = new SysUser();
        newSysUser.setUserId(sysUser.getUserId());
        newSysUser.setPassword(passwordField.getText());
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> viewModel.restPassword(newSysUser)).addRunnableInPlatformThread(() -> {
                dialog.close();
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("提示");
        getDialogContent().setContent(vBox);
        getDialogContent().show(rootPane.getScene());


    }

    private void showAuthRoleDialog(SysUser sysUser) {

        ViewTuple<AuthRoleView, AuthRoleViewModel> load = FluentViewLoader.fxmlView(AuthRoleView.class).load();
        load.getViewModel().setUserId(sysUser.getUserId());
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("提交"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save()).addConsumerInPlatformThread(res -> {

                if (res) {
                    dialog.close();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("分配角色");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());


    }

    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();
        }
        return dialog;
    }


    /**
     * 删除
     *
     * @param userId 用户id
     */
    private void remove(String userId) {

        ProcessChain.create().addRunnableInExecutor(() -> viewModel.remove(userId)).addRunnableInPlatformThread(() -> {
            dialog.close();
            initData();
        }).onException(e -> e.printStackTrace()).run();

    }

    private void initData() {
        ProcessChain.create().addRunnableInPlatformThread(() -> loading.setVisible(true)).addRunnableInExecutor(() -> {
            viewModel.deptTree();
            viewModel.userList();
        }).addRunnableInPlatformThread(() -> treeView.setRoot(createDeptTreeRoot())).withFinal(() -> loading.setVisible(false)).onException(e -> e.printStackTrace()).run();
    }


}
