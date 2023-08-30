package com.lw.fx.client.view.system.dept;

import atlantafx.base.controls.Popover;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysDept;
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
public class DeptDialogView implements FxmlView<DeptDialogViewModel>, Initializable {

    @InjectViewModel
    private DeptDialogViewModel viewModel;
    @FXML
    private TextField deptNameField;

    @FXML
    private TextField emailField;

    @FXML
    private ToggleGroup group;

    @FXML
    private TextField leaderField;

    @FXML
    private Spinner<Integer> ordNumFeild;

    @FXML
    private TextField parentIdCombo;

    @FXML
    private TextField phoneField;

    @FXML
    private RadioButton statusRadio1;

    @FXML
    private RadioButton statusRadio2;

    private TreeView<SysDept> deptTreeView;

    private Popover deptTreePopover;


    /**
     * 初始化
     *
     * @param url            url
     * @param resourceBundle 资源包
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deptTreeView = new TreeView<SysDept>();
        deptTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.setSelectSysDept(newValue.getValue());

            }
            if (deptTreePopover != null) {
                deptTreePopover.hide();
            }

        });

        parentIdCombo.setEditable(false);
        parentIdCombo.setOnMouseClicked(event -> showDeptTreePopover(parentIdCombo));
        parentIdCombo.textProperty().bind(Bindings.createStringBinding(
                () -> viewModel.selectSysDeptProperty().getValue().getDeptName(), viewModel.selectSysDeptProperty())
        );
        deptTreeView.setCellFactory(new Callback<TreeView<SysDept>, TreeCell<SysDept>>() {
            @Override
            public TreeCell<SysDept> call(TreeView<SysDept> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(SysDept item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(new Label(item.getDeptName()));
                        }
                    }
                };
            }
        });


        deptNameField.textProperty().bindBidirectional(viewModel.deptNameProperty());

        ordNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
        viewModel.orderNumProperty().addListener((observable, oldValue, newValue) -> {
            ordNumFeild.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, newValue.intValue()));
        });
        ordNumFeild.valueProperty().addListener((observable, oldValue, newValue) -> viewModel.orderNumProperty().setValue(newValue));

        emailField.textProperty().bindBidirectional(viewModel.emailProperty());

        leaderField.textProperty().bindBidirectional(viewModel.leaderProperty());

        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());


        statusRadio1.setUserData("0");
        statusRadio2.setUserData("1");
        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (group.getSelectedToggle() != null) {
                viewModel.statusProperty().setValue(group.getSelectedToggle().getUserData().toString());
            }
        });
        viewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.contains("0", newValue)) {
                statusRadio1.setSelected(true);
            } else {
                statusRadio2.setSelected(true);
            }
        });


        initDeptTuree();
    }

    private TreeItem selItem = null;

    /**
     * 创建根
     */
    private void createRoot() {
        SysDept rootDept = new SysDept();
        rootDept.setDeptId(0L);
        rootDept.setDeptName("主类目");
        var root = new TreeItem<SysDept>(rootDept);
        deptTreeView.setRoot(root);
        root.setExpanded(true);
        selItem = root;
        Map<Long, List<SysDept>> groupMap = viewModel.getAllDeptData().stream().collect(Collectors.groupingBy(SysDept::getParentId));
        List<SysDept> firstList = groupMap.get(0L);
        if (CollUtil.isNotEmpty(firstList)) {
            firstList.forEach(bean -> {
                var group = new TreeItem<>(bean);
                root.getChildren().add(group);
                if (ObjectUtil.equals(viewModel.getSelectSysDept().getDeptId(), bean.getDeptId())) {
                    selItem = group;
                }
                List<SysDept> childs = groupMap.get(bean.getDeptId());
                if (CollUtil.isNotEmpty(childs)) {
                    generateTree(group, childs, groupMap);
                }
            });
        }
        deptTreeView.getSelectionModel().select(selItem);

    }

    /**
     * 生成树
     *
     * @param parent      父
     * @param SysDeptList 系统菜单列表
     * @param groupMap    组织图
     */
    private void generateTree(TreeItem<SysDept> parent, List<SysDept> SysDeptList, Map<Long, List<SysDept>> groupMap) {
        SysDeptList.forEach(bean -> {
            var group = new TreeItem<>(bean);
            parent.getChildren().add(group);
            List<SysDept> childs = groupMap.get(bean.getDeptId());
            if (ObjectUtil.equals(viewModel.getSelectSysDept().getDeptId(), bean.getDeptId())) {
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
    private void showDeptTreePopover(Node source) {
        if (deptTreePopover == null) {
            deptTreePopover = new Popover(deptTreeView);
            deptTreePopover.setHeaderAlwaysVisible(false);
            deptTreePopover.setDetachable(false);
            deptTreePopover.setArrowLocation(TOP_CENTER);
        }
        deptTreeView.setPrefWidth(parentIdCombo.getWidth() - 40);
//        Bounds bounds = source.localToScreen(source.getBoundsInLocal());
        deptTreePopover.show(source);
    }

    /**
     * 初始化菜单turee
     * init弥尼树
     */
    private void initDeptTuree() {
        ProcessChain.create()
                .addRunnableInExecutor(() -> viewModel.deptList())
                .addRunnableInPlatformThread(() -> createRoot()).onException(e -> e.printStackTrace())
                .run();
    }

}
