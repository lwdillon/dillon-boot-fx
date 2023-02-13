package org.dillon.fx.view.system.menu;

import atlantafx.base.controls.RingProgressIndicator;
import cn.hutool.core.collection.CollUtil;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
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
import static org.dillon.fx.view.system.menu.MenuManageViewModel.REFRESH;

public class MenuManageView implements FxmlView<MenuManageViewModel>, Initializable {

    @InjectViewModel
    private MenuManageViewModel viewModel;

    @FXML
    private HBox load;
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
        viewModel.getListCommand().execute();
        viewModel.subscribe(REFRESH,(key,p)->{
            viewModel.getListCommand().execute();
        });
        var ring = new RingProgressIndicator(0, false);
        ring.setMinSize(45, 45);
        ring.progressProperty().bind(Bindings.createDoubleBinding(
                () -> load.isVisible() ? -1d : 0d, load.visibleProperty())
        );
        load.getChildren().add(ring);

        load.visibleProperty().bind(viewModel.getListCommand().runningProperty());
        load.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                createRoot();
            }
        });
        searchBut.setOnAction(event -> viewModel.getListCommand().execute());
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

               TreeTableCell cell=new TreeTableCell<SysMenu,String>(){
                   @Override
                   protected void updateItem(String item, boolean empty) {
                       super.updateItem(item, empty);
                       if (empty) {
                           setText(null);
                           setGraphic(null);
                       } else {

                           Button addBut = new Button("新增");
                           addBut.setOnAction(event -> showEditDialog(getTableRow().getItem(),false));
                           addBut.setGraphic(FontIcon.of(Feather.PLUS));
                           addBut.getStyleClass().addAll(FLAT, ACCENT);
                           Button editBut = new Button("修改");
                           editBut.setOnAction(event -> showEditDialog(getTableRow().getItem(),true));
                           editBut.setGraphic(FontIcon.of(Feather.EDIT));
                           editBut.getStyleClass().addAll(FLAT, ACCENT);
                           Button remBut = new Button("删除");
                           remBut.setOnAction(event -> viewModel.remove(getTableRow().getItem().getMenuId()));
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

    private void showEditDialog(SysMenu sysMenu,boolean isEdit) {

        ViewTuple<MenuDialogView, MenuDialogViewModel> load = FluentViewLoader.fxmlView(MenuDialogView.class).load();
        MenuManagerDialog themeRepoManagerDialog = new MenuManagerDialog(load,sysMenu,isEdit);
        themeRepoManagerDialog.setTitle("添加菜单");
        getOverlay().setContent(themeRepoManagerDialog, HPos.CENTER);

        themeRepoManagerDialog.setOnCloseRequest(() -> {
            overlay.removeContent();
            overlay.toBack();
            viewModel.getListCommand().execute();
        });
        getOverlay().toFront();
    }


}
