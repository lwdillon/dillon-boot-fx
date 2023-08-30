package com.lw.fx.client.view.system.user;

import atlantafx.base.controls.Popover;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.pinyin.PinyinUtil;
import com.lw.fx.client.domain.SysPost;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.domain.vo.TreeSelect;
import com.lw.fx.client.view.control.FilterableTreeItem;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class UserInfoView implements FxmlView<UserInfoViewModel>, Initializable {

    @InjectViewModel
    private UserInfoViewModel viewModel;
    @FXML
    private VBox rootPane;
    @FXML
    private TextField nickNameField;
    @FXML
    private TextField deptField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phonenumberField;
    @FXML
    private TextField userNameField;
    @FXML
    private FlowPane postPane;

    @FXML
    private ComboBox<String> sexCombo;
    @FXML
    private FlowPane rolesPane;

    @FXML
    private ScrollPane postSp;
    @FXML
    private ScrollPane roleSp;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextArea remarkArea;
    @FXML
    private ToggleGroup stateGroup;
    @FXML
    private RadioButton statusRadio;

    @FXML
    private RadioButton statusStopRadio;

    private ListView<SysRole> roleListView;
    private ListView<SysPost> postListView;

    private TreeView<TreeSelect> deptTreeView;

    private Popover deptTreePopover;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        deptField.textProperty().bind(Bindings.createStringBinding(
                () -> viewModel.selDeptProperty().getValue().getLabel(), viewModel.selDeptProperty())
        );
        userNameField.textProperty().bindBidirectional(viewModel.userNameProperty());
        nickNameField.textProperty().bindBidirectional(viewModel.nickNameProperty());
        emailField.textProperty().bindBidirectional(viewModel.emailProperty());
        phonenumberField.textProperty().bindBidirectional(viewModel.phonenumberProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        remarkArea.textProperty().bindBidirectional(viewModel.remarkProperty());
        viewModel.sexProperty().addListener((observable, oldValue, newValue) -> sexCombo.getSelectionModel().select(Objects.equals(newValue, "0") ? "男" : Objects.equals(newValue, "1") ? "女" : "未知"));
        sexCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> viewModel.sexProperty().setValue(Objects.equals(newValue, "男") ? "0" : Objects.equals(newValue, "女") ? "1" : "2"));
        statusRadio.setUserData("0");
        statusStopRadio.setUserData("1");
        viewModel.statusProperty().addListener((observable, oldValue, newValue) -> {
            if (ObjectUtil.equals("0", newValue)) {
                statusRadio.setSelected(true);
            }
        });
        stateGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (stateGroup.getSelectedToggle() != null) {
                viewModel.statusProperty().setValue(stateGroup.getSelectedToggle().getUserData().toString());
            }
        });

        roleListView = new ListView<>();
        roleListView.setCellFactory(CheckBoxListCell.forListView(new Callback<SysRole, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(SysRole param) {
                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(param.isSelect());
                booleanProperty.addListener((observable, oldValue, newValue) -> {
                    param.setSelect(newValue);
                    if (newValue) {
                        viewModel.getSelRoleMap().put(param.getRoleId(), param);
                    } else {
                        viewModel.getSelRoleMap().remove(param.getRoleId());
                    }
                });
                return booleanProperty;
            }
        }, new StringConverter<SysRole>() {
            @Override
            public String toString(SysRole object) {
                return object.getRoleName();
            }

            @Override
            public SysRole fromString(String string) {
                return null;
            }
        }));
        roleListView.setItems(viewModel.getRoles());

        postListView = new ListView<>();
        postListView.setMaxHeight(240);
        postListView.setCellFactory(CheckBoxListCell.forListView(new Callback<SysPost, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(SysPost param) {
                SimpleBooleanProperty booleanProperty = new SimpleBooleanProperty(param.isSelect());
                booleanProperty.addListener((observable, oldValue, newValue) -> {
                    param.setSelect(newValue);
                    if (newValue) {
                        viewModel.getSelPostMap().put(param.getPostId(), param);
                    } else {
                        viewModel.getSelPostMap().remove(param.getPostId());
                    }
                });
                return booleanProperty;
            }

        }, new StringConverter<SysPost>() {
            @Override
            public String toString(SysPost object) {
                return object.getPostName();
            }

            @Override
            public SysPost fromString(String string) {
                return null;
            }
        }));
        postListView.setItems(viewModel.getPosts());
        postListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.err.println(newValue);
        });
        deptTreeView = new TreeView<TreeSelect>();
        deptTreeView.setShowRoot(false);
        deptTreeView.setCellFactory(new Callback<TreeView<TreeSelect>, TreeCell<TreeSelect>>() {
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
        var deptPopover = new Popover(deptTreeView);
        deptPopover.setHeaderAlwaysVisible(false);
        deptPopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        deptField.setOnMouseClicked(e -> deptPopover.show(deptField));

        deptTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                viewModel.setSelDept(newValue.getValue());
            }
            deptPopover.hide();

        });


        var rolePopover = new Popover(roleListView);
        rolePopover.setHeaderAlwaysVisible(false);
        rolePopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        roleSp.setOnMouseClicked(e -> rolePopover.show(roleSp));


        var postPopover = new Popover(postListView);
        postPopover.setHeaderAlwaysVisible(false);
        postPopover.setArrowLocation(Popover.ArrowLocation.TOP_CENTER);
        postSp.setOnMouseClicked(e -> postPopover.show(postSp));


        viewModel.getDeptTreeList().addListener(new ListChangeListener<TreeSelect>() {
            @Override
            public void onChanged(Change<? extends TreeSelect> c) {
                deptTreeView.setRoot(createDeptTreeRoot());

            }
        });
        Bindings.bindContent(postPane.getChildren(), viewModel.getSelPosts());
        Bindings.bindContent(rolesPane.getChildren(), viewModel.getSelRoles());
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

}
