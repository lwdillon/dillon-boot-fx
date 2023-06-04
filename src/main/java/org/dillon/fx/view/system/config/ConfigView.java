package org.dillon.fx.view.system.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.*;
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
import org.dillon.fx.domain.SysConfig;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.PagingControl;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class ConfigView implements FxmlView<ConfigViewModel>, Initializable {

    @InjectViewModel
    private ConfigViewModel configViewModel;

    @FXML
    private Button addBut;

    @FXML
    private TableColumn<?, ?> configIdCol;

    @FXML
    private TableColumn<?, ?> configKeyCol;

    @FXML
    private TextField configKeyField;

    @FXML
    private TableColumn<SysConfig, String> configNameCol;

    @FXML
    private TextField configNameField;

    @FXML
    private TableColumn<SysConfig, Boolean> configTypeCol;

    @FXML
    private TableColumn<SysConfig, String> configValueCol;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<SysConfig, Date> createTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button editBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<SysConfig, String > optCol;

    @FXML
    private TableColumn<?, ?> remarkCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysConfig, Boolean> selCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysConfig> tableView;

    private MFXProgressSpinner loading;

    private MFXStageDialog dialog;

    private MFXGenericDialog dialogContent;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(configViewModel.totalProperty());
        configViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        configViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        configViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            configViewModel.queryConfigDataList();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            configViewModel.queryConfigDataList();
        });
        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        configNameField.textProperty().bindBidirectional(configViewModel.configNameProperty());
        configKeyField.textProperty().bindBidirectional(configViewModel.configKeyProperty());
        statusCombo.valueProperty().bindBidirectional(configViewModel.configTypeProperty());
        startDatePicker.valueProperty().bindBidirectional(configViewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(configViewModel.endDateProperty());
        searchBut.setOnAction(event -> configViewModel.queryConfigDataList());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> configViewModel.reset());
        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showConfigDataInfoDialog(tableView.getSelectionModel().getSelectedItem().getConfigId());
        });
        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            configViewModel.getSysConfigs().forEach(config -> {
                if (config.isSelect()) {
                    delIds.add(config.getConfigId());
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
                            setText(ObjectUtil.equal("Y", item) ? "是" : ObjectUtil.equal("N", item) ? "否" : "全部");
                        }
                    }
                };
            }
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);
        configNameCol.setCellValueFactory(new PropertyValueFactory<>("configName"));
        configTypeCol.setCellValueFactory(new PropertyValueFactory<>("configType"));
        configValueCol.setCellValueFactory(new PropertyValueFactory<>("configValue"));
        configKeyCol.setCellValueFactory(new PropertyValueFactory<>("configKey"));
        configIdCol.setCellValueFactory(new PropertyValueFactory<>("configId"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));

        configTypeCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var item = ObjectUtil.equal("Y", row.getConfigType());
            return new SimpleBooleanProperty(item);
        });
        configTypeCol.setCellFactory(col -> {
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
                            state.setText("是");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        } else {
                            state.setText("否");
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
                        editBut.setOnAction(event -> showConfigDataInfoDialog(getTableRow().getItem().getConfigId()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(CollUtil.newArrayList(getTableRow().getItem().getConfigId())));
                        remBut.setGraphic(FontIcon.of(Feather.TRASH));
                        remBut.getStyleClass().addAll(FLAT, ACCENT);


                        HBox box = new HBox(editBut, remBut);
                        box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                        setGraphic(box);
                    }
                }
            };
        });


        createTimeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        createTimeCol.setCellFactory(new Callback<TableColumn<SysConfig, Date>, TableCell<SysConfig, Date>>() {
            @Override
            public TableCell<SysConfig, Date> call(TableColumn<SysConfig, Date> param) {
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
        tableView.setItems(configViewModel.getSysConfigs());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        addBut.setOnAction(event -> showConfigDataInfoDialog(null));

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

    private void showConfigDataInfoDialog(Long configTypeId) {

        ViewTuple<ConfigInfoView, ConfigInfoViewModel> load = FluentViewLoader.fxmlView(ConfigInfoView.class).load();
        load.getViewModel().updateSysConfigInfo(configTypeId);
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(configTypeId))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    configViewModel.queryConfigDataList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(configTypeId) ? "编辑参数" : "添加参数");
        getDialogContent().setContent(load.getView());
        getDialog().showDialog();
    }


    private void showDelDialog(List<Long> configIds) {

        if (CollUtil.isEmpty(configIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> configViewModel.del(CollUtil.join(configIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                configViewModel.queryConfigDataList();
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + configIds + "的参数吗？"));
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
