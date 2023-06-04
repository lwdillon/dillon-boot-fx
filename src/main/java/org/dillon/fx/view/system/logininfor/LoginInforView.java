package org.dillon.fx.view.system.logininfor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.MvvmFX;
import io.datafx.core.concurrent.ProcessChain;
import io.github.palexdev.materialfx.controls.MFXProgressSpinner;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Callback;
import org.dillon.fx.domain.SysLogininfor;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.PagingControl;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class LoginInforView implements FxmlView<LoginInforViewModel>, Initializable {

    @InjectViewModel
    private LoginInforViewModel loginInforViewModel;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<SysLogininfor, Date> accessTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button emptyBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<?, ?> infoIdCol;

    @FXML
    private TableColumn<?, ?> userNameCol;

    @FXML
    private TableColumn<?, ?> ipaddrCol;

    @FXML
    private TextField userNameField;

    @FXML
    private TableColumn<?, ?> msgCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysLogininfor, Boolean> selCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<SysLogininfor, Boolean> statusCol;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysLogininfor> tableView;


    @FXML
    private TextField ipaddrField;

    private MFXProgressSpinner loading;

    private MFXStageDialog dialog;

    private MFXGenericDialog dialogContent;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bind(loginInforViewModel.totalProperty());
        loginInforViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        loginInforViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        loginInforViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            loginInforViewModel.queryLogininforList();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            loginInforViewModel.queryLogininforList();
        });

        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        ipaddrField.textProperty().bindBidirectional(loginInforViewModel.ipaddrProperty());
        userNameField.textProperty().bindBidirectional(loginInforViewModel.userNameProperty());
        statusCombo.valueProperty().bindBidirectional(loginInforViewModel.statusProperty());
        startDatePicker.valueProperty().bindBidirectional(loginInforViewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(loginInforViewModel.endDateProperty());
        searchBut.setOnAction(event -> loginInforViewModel.queryLogininforList());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> loginInforViewModel.reset());

        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            loginInforViewModel.getSysLogininfors().forEach(operLog -> {
                if (operLog.isSelect()) {
                    delIds.add(operLog.getInfoId());
                }
            });
            showDelDialog(delIds);
        });
        statusCombo.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(ObjectUtil.equal("0", item) ? "成功" : ObjectUtil.equal("1", item) ? "失败" : "全部");
                        }
                    }
                };
            }
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);
        infoIdCol.setCellValueFactory(new PropertyValueFactory<>("infoId"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        ipaddrCol.setCellValueFactory(new PropertyValueFactory<>("ipaddr"));
        msgCol.setCellValueFactory(new PropertyValueFactory<>("msg"));

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


        accessTimeCol.setCellValueFactory(new PropertyValueFactory<>("accessTime"));
        accessTimeCol.setCellFactory(new Callback<TableColumn<SysLogininfor, Date>, TableCell<SysLogininfor, Date>>() {
            @Override
            public TableCell<SysLogininfor, Date> call(TableColumn<SysLogininfor, Date> param) {
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

        tableView.setItems(loginInforViewModel.getSysLogininfors());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


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

    private void showLogininforInfoDialog(Long operLogTypeId) {

//        ViewTuple<DictTypeInfoView, DictTypeInfoViewModel> load = FluentViewLoader.fxmlView(DictTypeInfoView.class).load();
//        getDialogContent().clearActions();
//        load.getViewModel().updateSysLogininforInfo(operLogTypeId);
//        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
//            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(operLogTypeId))).addConsumerInPlatformThread(r -> {
//                if (r) {
//                    dialog.close();
//                    operlogViewModel.queryLogininforList();
//                }
//            }).onException(e -> e.printStackTrace()).run();
//        }));
//        getDialogContent().setShowAlwaysOnTop(false);
//        getDialogContent().setShowMinimize(false);
//
//        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
//        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(operLogTypeId) ? "编辑字典类型" : "添加字典类型");
//        getDialogContent().setContent(load.getView());
//        getDialog().showDialog();
    }


    private void showDelDialog(List<Long> operLogIds) {

        if (CollUtil.isEmpty(operLogIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> loginInforViewModel.del(CollUtil.join(operLogIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                loginInforViewModel.queryLogininforList();
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + operLogIds + "的字典类型吗？"));
        getDialog().showDialog();
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
