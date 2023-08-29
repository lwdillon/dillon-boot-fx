package com.lw.fx.client.view.system.menu;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.fx.client.domain.SysMenu;
import com.lw.fx.client.icon.WIcon;
import com.lw.fx.client.view.control.WFXGenericDialog;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static atlantafx.base.theme.Styles.*;

/**
 * 菜单管理视图
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuManageView implements FxmlView<MenuManageViewModel>, Initializable {

    @InjectViewModel
    private MenuManageViewModel viewModel;


    private WFXGenericDialog dialog;

    @FXML
    private VBox content;

    @FXML
    private StackPane root;
    private RingProgressIndicator load;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> statusCombo;
    @FXML
    private Button addBut;
    @FXML
    private Button restBut;
    @FXML
    private TreeTableView<SysMenu> treeTableView;
    @FXML
    private TreeTableColumn<SysMenu, String> nameCol;
    @FXML
    private TreeTableColumn<SysMenu, String> iconCol;
    @FXML
    private TreeTableColumn<SysMenu, String> sortCol;
    @FXML
    private TreeTableColumn<SysMenu, String> authCol;
    @FXML
    private TreeTableColumn<SysMenu, String> comPathCol;
    @FXML
    private TreeTableColumn<SysMenu, Boolean> stateCol;
    @FXML
    private TreeTableColumn<SysMenu, Date> createTime;
    @FXML
    private TreeTableColumn<SysMenu, String> optCol;

    @FXML
    private Button searchBut;
    @FXML
    private ToggleButton expansionBut;


    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        load = new RingProgressIndicator();
        load.disableProperty().bind(load.visibleProperty().not());
        load.visibleProperty().bindBidirectional(content.disableProperty());
        root.getChildren().add(load);

        addBut.setOnAction(event -> showEditDialog(new SysMenu(), false));
        addBut.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
        searchField.textProperty().bindBidirectional(viewModel.searchTextProperty());
        statusCombo.valueProperty().bindBidirectional(viewModel.statusTextProperty());
        searchBut.setOnAction(event -> query());
        searchBut.getStyleClass().addAll(ACCENT);

        restBut.setOnAction(event -> {
            viewModel.rest();
            query();
        });
        expansionBut.selectedProperty().addListener((observable, oldValue, newValue) -> treeExpandedAll(treeTableView.getRoot(), newValue));
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("menuName"));
        iconCol.setStyle("-fx-alignment: CENTER");
        iconCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("icon"));
        iconCol.setCellFactory(new Callback<TreeTableColumn<SysMenu, String>, TreeTableCell<SysMenu, String>>() {
            @Override
            public TreeTableCell<SysMenu, String> call(TreeTableColumn<SysMenu, String> param) {
                return new TreeTableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (StrUtil.isEmpty(item)) {
                            setGraphic(null);
                            return;
                        }

                        Label label = new Label();
                        if (StrUtil.equals("#", item)) {
                            label.setText(item);
                        } else {
                            label.setGraphic(FontIcon.of(WIcon.findByDescription("lw-" + item)));
                        }

                        setGraphic(label);
                    }
                };
            }
        });
        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderNum"));
        sortCol.setStyle("-fx-alignment: CENTER");
        authCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("perms"));
        authCol.setStyle("-fx-alignment: CENTER");
        comPathCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("path"));
        comPathCol.setStyle("-fx-alignment: CENTER");
        stateCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var item = ObjectUtil.equal("0", row.getValue().getStatus());
            return new SimpleBooleanProperty(item);
        });
        stateCol.setStyle("-fx-alignment: CENTER");
        stateCol.setCellFactory(col -> {
            return new TreeTableCell<>() {
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
        createTime.setCellValueFactory(new TreeItemPropertyValueFactory<>("createTime"));
        createTime.setStyle("-fx-alignment: CENTER");
        createTime.setCellFactory(new Callback<TreeTableColumn<SysMenu, Date>, TreeTableCell<SysMenu, Date>>() {
            @Override
            public TreeTableCell<SysMenu, Date> call(TreeTableColumn<SysMenu, Date> param) {
                return new TreeTableCell<>() {
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
        optCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("menuName"));
        optCol.setCellFactory(new Callback<TreeTableColumn<SysMenu, String>, TreeTableCell<SysMenu, String>>() {
            @Override
            public TreeTableCell<SysMenu, String> call(TreeTableColumn<SysMenu, String> param) {

                TreeTableCell cell = new TreeTableCell<SysMenu, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {

                            Button addBut = new Button("新增");
                            addBut.setOnAction(event -> showEditDialog(getTableRow().getItem(), false));
                            addBut.setGraphic(FontIcon.of(Feather.PLUS));
                            addBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button editBut = new Button("修改");
                            editBut.setOnAction(event -> showEditDialog(getTableRow().getItem(), true));
                            editBut.setGraphic(FontIcon.of(Feather.EDIT));
                            editBut.getStyleClass().addAll(FLAT, ACCENT);
                            Button remBut = new Button("删除");
                            remBut.setOnAction(event -> showDelDialog(getTableRow().getItem()));
                            remBut.setGraphic(FontIcon.of(Feather.TRASH));
                            remBut.getStyleClass().addAll(FLAT, ACCENT);
                            HBox box = new HBox(addBut, editBut, remBut);
                            box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                            setGraphic(box);
                        }
                    }
                };
                return cell;
            }
        });

        toggleStyleClass(treeTableView, Tweaks.ALT_ICON);
        query();

    }

    /**
     * 创建树项目根
     * 创建根
     */
    private void createTreeItemRoot() {

        var root = new TreeItem<SysMenu>(new SysMenu());

        List<SysMenu> treeList = viewModel.getMenuList();

        if (CollUtil.isNotEmpty(treeList)) {
            treeList.forEach(sysMenu -> {
                var group = new TreeItem<SysMenu>(sysMenu);
                root.getChildren().add(group);
                List<SysMenu> children = sysMenu.getChildren();

                if (CollUtil.isNotEmpty(children)) {
                    generateTree(group, children);
                }
            });
        }
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        root.setExpanded(true);

    }


    /**
     * 树扩展所有
     *
     * @param root     根
     * @param expanded 扩大
     */
    private void treeExpandedAll(TreeItem<SysMenu> root, boolean expanded) {
        for (TreeItem<SysMenu> child : root.getChildren()) {
            child.setExpanded(expanded);
            if (!child.getChildren().isEmpty()) {
                treeExpandedAll(child, expanded);
            }
        }
    }

    /**
     * 生成树
     *
     * @param parent   父
     * @param treeList 树列表
     */
    private void generateTree(TreeItem<SysMenu> parent, List<SysMenu> treeList) {
        treeList.forEach(treeNode -> {
            var group = new TreeItem<SysMenu>(treeNode);
            parent.getChildren().add(group);
            List<SysMenu> children = treeNode.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                generateTree(group, children);
            }
        });
    }


    /**
     * 显示编辑对话框
     *
     * @param sysMenu 系统菜单
     * @param isEdit  是编辑
     */
    private void showEditDialog(SysMenu sysMenu, boolean isEdit) {


        ViewTuple<MenuDialogView, MenuDialogViewModel> load = FluentViewLoader.fxmlView(MenuDialogView.class).load();
        SysMenu selMenu = new SysMenu();
        selMenu.setMenuId(isEdit ? sysMenu.getParentId() : sysMenu.getMenuId());
        load.getViewModel().setSysMenu(isEdit ? sysMenu : new SysMenu());
        load.getViewModel().setSelectSysMenu(selMenu);

        getDialogContent().clearActions();
        getDialogContent().addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    save(load.getViewModel(), isEdit);
                })
        );


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(isEdit ? "编辑菜单" : "添加菜单");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(root.getScene());


    }


    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();

        }
        return dialog;
    }


    /**
     * 显示del对话框
     *
     * @param sysMenu 系统菜单
     */
    private void showDelDialog(SysMenu sysMenu) {
        getDialogContent().clearActions();
        getDialogContent().addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    remove(sysMenu.getMenuId());
                })
        );


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除名称为" + sysMenu.getMenuName() + "的数据项？"));
        getDialogContent().show(root.getScene());
    }

    /**
     * 查询
     */
    private void query() {
        ProcessChain.create().addRunnableInPlatformThread(() -> load.setVisible(true))
                .addRunnableInExecutor(() -> viewModel.query())
                .addRunnableInPlatformThread(() -> createTreeItemRoot()).withFinal(() -> {
                    load.setVisible(false);
                }).onException(e -> e.printStackTrace())
                .run();
    }

    /**
     * 保存
     *
     * @param menuDialogViewModel 菜单对话框视图模型
     * @param isEdit              是编辑
     */
    private void save(MenuDialogViewModel menuDialogViewModel, final boolean isEdit) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> menuDialogViewModel.save(isEdit))
                .addConsumerInPlatformThread(r -> {
                    if (r) {
                        dialog.close();
                        query();
                    }
                }).onException(e -> e.printStackTrace())
                .run();

    }

    /**
     * 删除
     *
     * @param menuId 菜单id
     */
    private void remove(Long menuId) {

        ProcessChain.create()
                .addRunnableInExecutor(() -> viewModel.remove(menuId))
                .addRunnableInPlatformThread(() -> {
                    dialog.close();
                    query();
                }).onException(e -> e.printStackTrace())
                .run();

    }


}
