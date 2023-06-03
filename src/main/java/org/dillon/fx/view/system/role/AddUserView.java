package org.dillon.fx.view.system.role;

import atlantafx.base.controls.ToggleSwitch;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.dillon.fx.domain.SysUser;
import org.dillon.fx.domain.vo.TreeSelect;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.PagingControl;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Tweaks.*;

public class AddUserView implements FxmlView<AddUserViewModel>, Initializable {

    @InjectViewModel
    private AddUserViewModel viewModel;

    @FXML
    private CheckBox selAllCheckBox;
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
    private HBox contentPane;
    @FXML
    private TableView<SysUser> tableView;
    @FXML
    private Button searchBut;

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
    private VBox pagePane;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        pagePane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bind(viewModel.totalProperty());
        viewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        viewModel.pageSizeProperty().bind(pagingControl.pageSizeProperty());
        pagingControl.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            viewModel.unallocatedList();
        });


        searchBut.getStyleClass().addAll(ACCENT);
        searchBut.setOnAction(event -> viewModel.unallocatedList());
        resetBut.setOnAction(event -> viewModel.reset());
        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);
        userSearchField.textProperty().bindBidirectional(viewModel.userNameProperty());
        phoneField.textProperty().bindBidirectional(viewModel.phoneProperty());
        resetBut.setOnAction(event -> viewModel.reset());


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
