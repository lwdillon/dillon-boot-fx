package com.lw.fx.client.view.system.dept;

import atlantafx.base.controls.RingProgressIndicator;
import atlantafx.base.theme.Tweaks;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysDept;
import com.lw.fx.client.util.Lazy;
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
 * 部门管理视图
 *
 * @author wenli
 * @date 2023/02/15
 */
public class DeptManageView implements FxmlView<DeptManageViewModel>, Initializable {

    @InjectViewModel
    private DeptManageViewModel viewModel;


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
    private TreeTableView<SysDept> treeTableView;
    @FXML
    private TreeTableColumn<SysDept, String> nameCol;


    @FXML
    private TreeTableColumn<SysDept, String> sortCol;

    @FXML
    private TreeTableColumn<SysDept, Boolean> stateCol;
    @FXML
    private TreeTableColumn<SysDept, Date> createTime;
    @FXML
    private TreeTableColumn<SysDept, String> optCol;

    @FXML
    private Button searchBut;
    @FXML
    private ToggleButton expansionBut;

    private Lazy<WFXGenericDialog> optDialog;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        optDialog = new Lazy<>(() -> {
            var dialog = new WFXGenericDialog();
            dialog.setClearOnClose(true);
            return dialog;
        });

        load = new RingProgressIndicator(0, false);
        load.disableProperty().bind(load.visibleProperty().not());
        load.visibleProperty().bindBidirectional(content.disableProperty());
        root.getChildren().add(load);

        addBut.setOnAction(event -> showEditDialog(new SysDept(), false));
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
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("deptName"));

        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderNum"));
        sortCol.setStyle("-fx-alignment: CENTER");

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


        createTime.setCellFactory(new Callback<TreeTableColumn<SysDept, Date>, TreeTableCell<SysDept, Date>>() {
            @Override
            public TreeTableCell<SysDept, Date> call(TreeTableColumn<SysDept, Date> param) {
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

        optCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("deptName"));
        optCol.setCellFactory(new Callback<TreeTableColumn<SysDept, String>, TreeTableCell<SysDept, String>>() {
            @Override
            public TreeTableCell<SysDept, String> call(TreeTableColumn<SysDept, String> param) {

                TreeTableCell cell = new TreeTableCell<SysDept, String>() {
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

        var root = new TreeItem<SysDept>(new SysDept());

        List<SysDept> treeList = viewModel.getDeptList();

        if (CollUtil.isNotEmpty(treeList)) {
            treeList.forEach(SysDept -> {
                var group = new TreeItem<SysDept>(SysDept);
                root.getChildren().add(group);
                List<SysDept> children = SysDept.getChildren();

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
    private void treeExpandedAll(TreeItem<SysDept> root, boolean expanded) {
        for (TreeItem<SysDept> child : root.getChildren()) {
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
    private void generateTree(TreeItem<SysDept> parent, List<SysDept> treeList) {
        treeList.forEach(treeNode -> {
            var group = new TreeItem<SysDept>(treeNode);
            parent.getChildren().add(group);
            List<SysDept> children = treeNode.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                generateTree(group, children);
            }
        });
    }


    /**
     * 显示编辑对话框
     *
     * @param SysDept 系统部门
     * @param isEdit  是编辑
     */
    private void showEditDialog(SysDept SysDept, boolean isEdit) {


        ViewTuple<DeptDialogView, DeptDialogViewModel> load = FluentViewLoader.fxmlView(DeptDialogView.class).load();
        SysDept selDept = new SysDept();
        selDept.setDeptId(isEdit ? SysDept.getParentId() : SysDept.getDeptId());
        load.getViewModel().setSysDept(isEdit ? SysDept : new SysDept());
        load.getViewModel().setSelectSysDept(selDept);

        var dialog = optDialog.get();
        dialog.clearActions();
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    save(load.getViewModel(), isEdit);
                })
        );

        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText(isEdit ? "编辑部门" : "添加部门");
        dialog.setContent(load.getView());
        dialog.show(root.getScene());


    }


    /**
     * 显示del对话框
     *
     * @param SysDept 系统部门
     */
    private void showDelDialog(SysDept SysDept) {
        var dialog = optDialog.get();
        dialog.clearActions();
        dialog.addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    remove(SysDept.getDeptId());
                })
        );


        dialog.setHeaderIcon(FontIcon.of(Feather.INFO));
        dialog.setHeaderText("系统揭示");
        dialog.setContent(new Label("是否确认删除名称为" + SysDept.getDeptName() + "的数据项？"));
        dialog.show(root.getScene());
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
     * @param deptDialogViewModel 部门对话框视图模型
     * @param isEdit              是编辑
     */
    private void save(DeptDialogViewModel deptDialogViewModel, final boolean isEdit) {

        ProcessChain.create()
                .addSupplierInExecutor(() -> deptDialogViewModel.save(isEdit))
                .addConsumerInPlatformThread(r -> {
                    if (r) {
                        optDialog.get().close();
                        query();
                    }
                }).onException(e -> e.printStackTrace())
                .run();

    }

    /**
     * 删除
     *
     * @param deptId 部门id
     */
    private void remove(Long deptId) {

        ProcessChain.create()
                .addRunnableInExecutor(() -> viewModel.remove(deptId))
                .addRunnableInPlatformThread(() -> {
                    optDialog.get().close();
                    query();
                }).onException(e -> e.printStackTrace())
                .run();

    }


}
