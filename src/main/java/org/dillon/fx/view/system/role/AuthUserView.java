package org.dillon.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.dillon.fx.domain.SysRole;
import org.dillon.fx.domain.SysUser;
import org.dillon.fx.domain.SysUserRole;
import org.dillon.fx.domain.vo.TreeSelect;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.FilterableTreeItem;
import org.dillon.fx.view.control.OverlayDialog;
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
    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;
    private MFXProgressSpinner loading;
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
    private Pagination pagination;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        loading = new MFXProgressSpinner();
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
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        if (item) {
                            state.setText("正常");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        }else {
                            state.setText("停用");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7,7,7,7));
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
            addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }
        pagination.pageCountProperty().bind(viewModel.totalProperty());
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                return null;
            }
        });

    }

    private void showDialog(Long roleId) {
        ViewTuple<AddUserView, AddUserViewModel> load = FluentViewLoader.fxmlView(AddUserView.class).load();
        load.getViewModel().setRoleId(roleId);
        load.getViewModel().unallocatedList();
        getDialogContent().clearActions();

        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {

            ProcessChain.create().addSupplierInExecutor(() ->  load.getViewModel().save()).addConsumerInPlatformThread(res -> {

                if (res) {
                    dialog.close();
                    viewModel.allocatedList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("选择用户");
        getDialogContent().setContent(load.getView());
        getDialog().showDialog();
    }

    public MFXGenericDialog getDialogContent() {
        if (dialogContent == null) {
            dialogContent = MFXGenericDialogBuilder.build().makeScrollable(true).get();
            new CSSFragment(OverlayDialog.CSS).addTo(dialogContent);
        }
        return dialogContent;
    }

    public MFXStageDialog getDialog() {
        if (dialog == null) {
            dialog = MFXGenericDialogBuilder.build(dialogContent).toStageDialogBuilder().initOwner(rootPane.getScene().getWindow()).initModality(Modality.APPLICATION_MODAL).setDraggable(true).setTitle("提示！").setScrimPriority(ScrimPriority.WINDOW).setScrimOwner(true).get();
        }
        return dialog;
    }


    private static void addStyleClass(TableColumn<?, ?> c, String styleClass, String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
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
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("确认要取消该用户" + sysUser.getUserName() + "的角色吗？"));
        getDialog().showDialog();
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
            ProcessChain.create().addSupplierInExecutor(() -> viewModel.cancelAll(viewModel.getRoleId(), CollUtil.join(userIds,","))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    viewModel.allocatedList();
                }

            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否取消选中用户授权数据项？"));
        getDialog().showDialog();
    }
}
