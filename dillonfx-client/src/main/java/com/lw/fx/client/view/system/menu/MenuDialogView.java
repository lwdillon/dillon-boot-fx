package com.lw.fx.client.view.system.menu;

import atlantafx.base.controls.Popover;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysMenu;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static atlantafx.base.controls.Popover.ArrowLocation.TOP_CENTER;

/**
 * 视图菜单对话框
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuDialogView implements FxmlView<MenuDialogViewModel>, Initializable {

    @InjectViewModel
    private MenuDialogViewModel viewModel;
    @FXML
    private TextField parentIdCombo;

    @FXML
    private RadioButton menuTypeRadio1;
    @FXML
    private RadioButton menuTypeRadio2;
    @FXML
    private RadioButton menuTypeRadio3;

    @FXML
    private TextField iconField;

    @FXML
    private TextField menuNameField;

    @FXML
    private Spinner<Integer> ordNumFeild;

    @FXML
    private RadioButton frameRadio1;
    @FXML
    private RadioButton frameRadio2;

    @FXML
    private TextField pathField;

    @FXML
    private TextField componentField;

    @FXML
    private TextField permsField;

    @FXML
    private TextField queryField;


    @FXML
    private RadioButton cacheRadio1;

    @FXML
    private RadioButton cacheRadio2;

    @FXML
    private RadioButton visibleRadio1;

    @FXML
    private RadioButton visibleRadio2;

    @FXML
    private RadioButton statusRadio1;

    @FXML
    private RadioButton statusRadio2;

    private TreeView<SysMenu> menuTreeView;

    private Popover menuTreePopover;

    @FXML
    private ToggleGroup group1;

    @FXML
    private ToggleGroup group2;

    @FXML
    private ToggleGroup group3;

    @FXML
    private ToggleGroup group4;

    @FXML
    private ToggleGroup group5;

    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuTreeView = new TreeView<SysMenu>();
        menuTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.setSelectSysMenu(newValue.getValue());

            }
            if (menuTreePopover != null) {
                menuTreePopover.hide();
            }

        });

        parentIdCombo.setEditable(false);
        parentIdCombo.setOnMouseClicked(event -> showMenuTreePopover(parentIdCombo));
        parentIdCombo.textProperty().bind(Bindings.createStringBinding(
                () -> viewModel.selectSysMenuProperty().getValue().getMenuName(), viewModel.selectSysMenuProperty())
        );
        menuTreeView.setCellFactory(new Callback<TreeView<SysMenu>, TreeCell<SysMenu>>() {
            @Override
            public TreeCell<SysMenu> call(TreeView<SysMenu> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(SysMenu item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(new Label(item.getMenuName()));
                        }
                    }
                };
            }
        });

        group1.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group1.getSelectedToggle() != null) {
                viewModel.menuTypeProperty().setValue(group1.getSelectedToggle().getUserData().toString());
            }
        });

        menuTypeRadio1.setUserData("M");
        menuTypeRadio2.setUserData("C");
        menuTypeRadio3.setUserData("F");
        viewModel.menuTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.equals("M", newValue)) {
                menuTypeRadio1.setSelected(true);
            } else if (ObjectUtil.contains("C", newValue)) {
                menuTypeRadio2.setSelected(true);
            } else {
                menuTypeRadio3.setSelected(true);
            }
        });


        iconField.textProperty().bindBidirectional(viewModel.iconProperty());


        menuNameField.textProperty().bindBidirectional(viewModel.menuNameProperty());

        ordNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        viewModel.orderNumProperty().addListener((observable, oldValue, newValue) -> {
            ordNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        ordNumFeild.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.orderNumProperty().setValue(newValue));

        frameRadio1.setUserData("0");
        frameRadio2.setUserData("1");
        group2.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group2.getSelectedToggle() != null) {
                viewModel.isFrameProperty().setValue(group2.getSelectedToggle().getUserData().toString());
            }
        });

        viewModel.isFrameProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                frameRadio1.setSelected(true);
            } else {
                frameRadio2.setSelected(true);
            }
        });


        pathField.textProperty().bindBidirectional(viewModel.pathProperty());

        componentField.textProperty().bindBidirectional(viewModel.componentProperty());

        permsField.textProperty().bindBidirectional(viewModel.permsProperty());

        queryField.textProperty().bindBidirectional(viewModel.queryProperty());

        cacheRadio1.setUserData("0");
        cacheRadio2.setUserData("1");
        group3.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group3.getSelectedToggle() != null) {
                viewModel.isCacheProperty().setValue(group3.getSelectedToggle().getUserData().toString());
            }
        });
        viewModel.isCacheProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                cacheRadio1.setSelected(true);
            } else {
                cacheRadio2.setSelected(true);
            }
        });


        visibleRadio1.setUserData("0");
        visibleRadio2.setUserData("1");
        group4.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group4.getSelectedToggle() != null) {
                viewModel.visibleProperty().setValue(group4.getSelectedToggle().getUserData().toString());
            }
        });


        viewModel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                visibleRadio1.setSelected(true);
            } else {
                visibleRadio2.setSelected(true);
            }
        });


        statusRadio1.setUserData("0");
        statusRadio2.setUserData("1");
        group5.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group5.getSelectedToggle() != null) {
                viewModel.statusProperty().setValue(group5.getSelectedToggle().getUserData().toString());
            }
        });
        viewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                statusRadio1.setSelected(true);
            } else {
                statusRadio2.setSelected(true);
            }
        });


        initMenuTuree();
    }

    private TreeItem selItem = null;

    /**
     * 创建根
     */
    private void createRoot() {
        SysMenu rootMenu = new SysMenu();
        rootMenu.setMenuId(0L);
        rootMenu.setMenuName("主类目");
        var root = new TreeItem<SysMenu>(rootMenu);
        menuTreeView.setRoot(root);
        root.setExpanded(true);
        selItem = root;
        Map<Long, List<SysMenu>> groupMap = viewModel.getAllMenuData().stream().collect(Collectors.groupingBy(SysMenu::getParentId));
        List<SysMenu> firstList = groupMap.get(0L);
        if (CollUtil.isNotEmpty(firstList)) {
            firstList.forEach(bean -> {
                var group = new TreeItem<>(bean);
                root.getChildren().add(group);
                if (ObjectUtil.equals(viewModel.getSelectSysMenu().getMenuId(), bean.getMenuId())) {
                    selItem = group;
                }
                List<SysMenu> childs = groupMap.get(bean.getMenuId());
                if (CollUtil.isNotEmpty(childs)) {
                    generateTree(group, childs, groupMap);
                }
            });
        }
        menuTreeView.getSelectionModel().select(selItem);

    }

    /**
     * 生成树
     *
     * @param parent      父
     * @param sysMenuList 系统菜单列表
     * @param groupMap    组织图
     */
    private void generateTree(TreeItem<SysMenu> parent, List<SysMenu> sysMenuList, Map<Long, List<SysMenu>> groupMap) {
        sysMenuList.forEach(bean -> {
            var group = new TreeItem<>(bean);
            parent.getChildren().add(group);
            List<SysMenu> childs = groupMap.get(bean.getMenuId());
            if (ObjectUtil.equals(viewModel.getSelectSysMenu().getMenuId(), bean.getMenuId())) {
                selItem = group;
            }
            if (CollUtil.isNotEmpty(childs)) {
                generateTree(group, childs, groupMap);
            }
        });
    }


    /**
     * 显示弹出窗口菜单树
     *
     * @param source 源
     */
    private void showMenuTreePopover(Node source) {
        if (menuTreePopover == null) {
            menuTreePopover = new Popover(menuTreeView);
            menuTreePopover.setHeaderAlwaysVisible(false);
            menuTreePopover.setDetachable(false);
            menuTreePopover.setArrowLocation(TOP_CENTER);
        }
        menuTreeView.setPrefWidth(parentIdCombo.getWidth() - 40);
//        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        menuTreePopover.show(source);
    }

    /**
     * 初始化菜单turee
     * init弥尼树
     */
    private void initMenuTuree() {
        ProcessChain.create()
                .addRunnableInExecutor(() -> viewModel.menuList())
                .addRunnableInPlatformThread(() -> createRoot()).onException(e -> e.printStackTrace())
                .run();
    }

}
