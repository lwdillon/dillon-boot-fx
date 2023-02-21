package org.dillon.fx.view.system.user;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import io.datafx.core.concurrent.ProcessChain;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.dillon.fx.domain.SysUser;
import org.dillon.fx.domain.vo.TreeSelect;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.FilterableTreeItem;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.TreeItemPredicate;
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

    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;
    private MFXProgressSpinner loading;
    @FXML
    private StackPane rootPane;
    @FXML
    private Button addBut;
    @FXML
    private Pagination pagination;
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

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

            viewModel.setDeptSelectId(newValue.getValue().getId());
        });
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setCellValueFactory(new PropertyValueFactory<>("sel"));
        selCol.setEditable(true);
        idCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        nickNameCol.setCellValueFactory(new PropertyValueFactory<>("nickName"));
        deptCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var item = ObjectUtil.defaultIfNull(row.getDept().getDeptName(), "");
            return new SimpleStringProperty(item);
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
//                            editBut.setOnAction(event -> showEditDialog(getTableRow().getItem(), true));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
//                            remBut.setOnAction(event -> showDelDialog(getTableRow().getItem()));
                        remBut.setGraphic(FontIcon.of(Feather.TRASH));
                        remBut.getStyleClass().addAll(FLAT, ACCENT);

                        MenuButton moreBut = new MenuButton("更多");
                        moreBut.getItems().addAll(new MenuItem("重置密码"), new MenuItem("分配角色"));
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
        tableView.setItems(viewModel.getUserList());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }
//        pagination.pageCountProperty().bind(viewModel.totalProperty());
        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                return null;
            }
        });
        initListener();
        initData();
    }

    private void initListener() {

        addBut.setOnAction(event -> showEditDialog(new SysUser(),false));
    }

    private void createDeptTreeRoot() {

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
        treeView.setShowRoot(false);
        treeView.setRoot(rootNode);

        rootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            if (detpSearchField.getText() == null || detpSearchField.getText().isEmpty())
                return null;
            return TreeItemPredicate.create(actor -> {
                if (containsString(actor.getLabel(), detpSearchField.getText())) {

                    return true;
                } else {
                    return false;
                }
            });
        }, detpSearchField.textProperty()));
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

    /**
     * 显示编辑对话框
     *
     * @param sysMenu 系统菜单
     * @param isEdit  是编辑
     */
    private void showEditDialog(SysUser SysUser, boolean isEdit) {


        ViewTuple<UserInfoView, UserInfoViewModel> load = FluentViewLoader.fxmlView(UserInfoView.class).load();


        getDialogContent().clearActions();
        getDialogContent().addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
//                    save(load.getViewModel(), isEdit);
                })
        );
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(isEdit ? "编辑用户" : "添加用户");
        getDialogContent().setContent(load.getView());
        getDialog().showDialog();


    }


    public MFXGenericDialog getDialogContent() {
        if (dialogContent == null) {
            dialogContent = MFXGenericDialogBuilder.build()
                    .makeScrollable(true)
                    .get();
            new CSSFragment(OverlayDialog.CSS).addTo(dialogContent);
        }
        return dialogContent;
    }

    public MFXStageDialog getDialog() {
        if (dialog == null) {
            dialog = MFXGenericDialogBuilder.build(dialogContent)
                    .toStageDialogBuilder()
                    .initOwner(rootPane.getScene().getWindow())
                    .initModality(Modality.APPLICATION_MODAL)
                    .setDraggable(true)
                    .setTitle("提示！")
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();
        }
        return dialog;
    }


    private void initData() {
        ProcessChain.create().addRunnableInPlatformThread(() -> loading.setVisible(true)).
                addRunnableInExecutor(() -> {
                    viewModel.deptTree();
                    viewModel.userList();
                }).addRunnableInPlatformThread(() -> createDeptTreeRoot())
                .withFinal(() -> loading.setVisible(false)).onException(e -> e.printStackTrace()).
                run();
    }

    private static void addStyleClass(TableColumn<?, ?> c, String styleClass, String... excludes) {
        Objects.requireNonNull(c);
        Objects.requireNonNull(styleClass);

        if (excludes != null && excludes.length > 0) {
            c.getStyleClass().removeAll(excludes);
        }
        c.getStyleClass().add(styleClass);
    }
}
