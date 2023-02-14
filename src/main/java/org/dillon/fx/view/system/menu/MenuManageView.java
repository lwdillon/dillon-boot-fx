package org.dillon.fx.view.system.menu;

import cn.hutool.core.collection.CollUtil;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.main.Overlay;
import org.dillon.fx.vo.SysMenu;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static atlantafx.base.theme.Styles.ACCENT;
import static atlantafx.base.theme.Styles.FLAT;

public class MenuManageView implements FxmlView<MenuManageViewModel>, Initializable {

    @InjectViewModel
    private MenuManageViewModel viewModel;

    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;

    @FXML
    private VBox content;

    @FXML
    private StackPane root;
    @FXML
    private MFXProgressSpinner load;
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
    private TreeTableColumn<SysMenu, String> stateCol;
    @FXML
    private TreeTableColumn<SysMenu, String> createTime;
    @FXML
    private TreeTableColumn<SysMenu, String> optCol;

    @FXML
    private Button searchBut;
    @FXML
    private ToggleButton expansionBut;

    private Overlay overlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        load = new MFXProgressSpinner();
        load.disableProperty().bind(load.visibleProperty().not());
        load.visibleProperty().bindBidirectional(content.disableProperty());


        root.getChildren().add(load);

        searchBut.setOnAction(event -> query());
        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("menuName"));
        iconCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("icon"));
        sortCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderNum"));
        authCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("perms"));
        comPathCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("path"));
        stateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("status"));
        createTime.setCellValueFactory(new TreeItemPropertyValueFactory<>("createTime"));
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
        query();

    }

    private void createRoot() {

        var root = new TreeItem<SysMenu>(new SysMenu());
//        root.expandedProperty().bind(expansionBut.selectedProperty());
        Map<Long, List<SysMenu>> groupMap = viewModel.getAllData().stream().collect(Collectors.groupingBy(SysMenu::getParentId));

        List<SysMenu> firstList = groupMap.get(0L);

        if (CollUtil.isNotEmpty(firstList)) {
            firstList.forEach(bean -> {
                var group = new TreeItem<>(bean);
                root.getChildren().add(group);
                group.expandedProperty().bindBidirectional(expansionBut.selectedProperty());
                List<SysMenu> childs = groupMap.get(bean.getMenuId());

                if (CollUtil.isNotEmpty(childs)) {
                    generateTree(group, childs, groupMap);
                }
            });
        }
        treeTableView.setRoot(root);
        treeTableView.setShowRoot(false);
        root.setExpanded(true);
    }

    private void generateTree(TreeItem<SysMenu> parent, List<SysMenu> sysMenuList, Map<Long, List<SysMenu>> groupMap) {
        sysMenuList.forEach(bean -> {
            var group = new TreeItem<>(bean);
            parent.getChildren().add(group);
            parent.expandedProperty().bindBidirectional(expansionBut.selectedProperty());
            List<SysMenu> childs = groupMap.get(bean.getMenuId());

            if (CollUtil.isNotEmpty(childs)) {
                generateTree(group, childs, groupMap);
            }
        });
    }


    protected Overlay lookupOverlay() {
        return load.getScene() != null && load.getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay ov ? ov : null;
    }


    public Overlay getOverlay() {
        if (overlay == null) {
            this.overlay = lookupOverlay();
        }
        return overlay;
    }

    private void showEditDialog(SysMenu sysMenu, boolean isEdit) {


        ViewTuple<MenuDialogView, MenuDialogViewModel> load = FluentViewLoader.fxmlView(MenuDialogView.class).load();
        SysMenu selMenu = new SysMenu();
        selMenu.setMenuId(isEdit ? sysMenu.getParentId() : 0L);
        load.getViewModel().setSysMenu(isEdit ? sysMenu : new SysMenu());
        load.getViewModel().setSelectSysMenu(selMenu);

        getDialogContent().clearActions();
        getDialogContent().addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    save(load.getViewModel(), isEdit);
                })
        );
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(isEdit ? "编辑菜单" : "添加菜单");
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
                    .initOwner(root.getScene().getWindow())
                    .initModality(Modality.APPLICATION_MODAL)
                    .setDraggable(true)
                    .setTitle("提示！")
                    .setScrimPriority(ScrimPriority.WINDOW)
                    .setScrimOwner(true)
                    .get();
        }
        return dialog;
    }


    private void showDelDialog(SysMenu sysMenu) {
        getDialogContent().clearActions();
        getDialogContent().addActions(
                Map.entry(new Button("取消"), event -> dialog.close()),
                Map.entry(new Button("确定"), event -> {
                    del(sysMenu.getMenuId());
                })
        );
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除名称为" + sysMenu.getMenuName() + "的数据项？"));
        getDialog().showDialog();
    }

    private void query() {
        ProcessChain.create().addRunnableInPlatformThread(() -> load.setVisible(true))
                .addRunnableInExecutor(() -> viewModel.query())
                .addRunnableInPlatformThread(() -> createRoot()).withFinal(() -> {
                    load.setVisible(false);
                }).onException(e -> e.printStackTrace())
                .run();
    }

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

    private void del(Long menuId) {

        ProcessChain.create()
                .addRunnableInExecutor(() -> viewModel.remove(menuId))
                .addRunnableInPlatformThread(() -> {
                    dialog.close();
                    query();
                }).onException(e -> e.printStackTrace())
                .run();

    }


}
