package org.dillon.fx.view.system.operlog;

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
import org.dillon.fx.domain.SysDictData;
import org.dillon.fx.domain.SysDictType;
import org.dillon.fx.domain.SysOperLog;
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.PagingControl;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class OperLogView implements FxmlView<OperLogViewModel>, Initializable {

    @InjectViewModel
    private OperLogViewModel operlogViewModel;

    @FXML
    private TableColumn<SysOperLog, Integer> businessTypeCol;

    @FXML
    private ComboBox<SysDictData> businessTypeCombo;

    @FXML
    private VBox contentPane;

    @FXML
    private TableColumn<SysOperLog, Long> costTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button emptyBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<?, ?> operIdCol;

    @FXML
    private TableColumn<?, ?> operIpCol;

    @FXML
    private TableColumn<?, ?> operNameCol;

    @FXML
    private TextField operNameField;

    @FXML
    private TableColumn<SysOperLog, Date> operTimeCol;

    @FXML
    private TableColumn<SysOperLog, String> optCol;

    @FXML
    private TableColumn<?, ?> requestMethodCol;

    @FXML
    private Button resetBut;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysOperLog, Boolean> selCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<SysOperLog, Boolean> statusCol;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysOperLog> tableView;

    @FXML
    private TableColumn<?, ?> titleCol;

    @FXML
    private TextField titleField;

    private MFXProgressSpinner loading;

    private MFXStageDialog dialog;

    private MFXGenericDialog dialogContent;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(operlogViewModel.totalProperty());
        operlogViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        operlogViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        operlogViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            operlogViewModel.updateData();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            operlogViewModel.updateData();
        });
        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        titleField.textProperty().bindBidirectional(operlogViewModel.titleProperty());
        operNameField.textProperty().bindBidirectional(operlogViewModel.operNameProperty());
        businessTypeCombo.valueProperty().bindBidirectional(operlogViewModel.sysDictDataProperty());
        businessTypeCombo.setItems(operlogViewModel.getDictDataObservableList());
        businessTypeCombo.setCellFactory(new Callback<ListView<SysDictData>, ListCell<SysDictData>>() {
            @Override
            public ListCell<SysDictData> call(ListView<SysDictData> param) {
                final ListCell<SysDictData> cell = new ListCell<SysDictData>() {

                    @Override
                    public void updateItem(SysDictData item,
                                           boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getDictLabel());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        statusCombo.valueProperty().bindBidirectional(operlogViewModel.statusProperty());
        startDatePicker.valueProperty().bindBidirectional(operlogViewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(operlogViewModel.endDateProperty());
        searchBut.setOnAction(event -> operlogViewModel.updateData());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> operlogViewModel.reset());

        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            operlogViewModel.getSysOperLogs().forEach(operLog -> {
                if (operLog.isSelect()) {
                    delIds.add(operLog.getOperId());
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
        operIdCol.setCellValueFactory(new PropertyValueFactory<>("operId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        businessTypeCol.setCellValueFactory(new PropertyValueFactory<>("businessType"));
        requestMethodCol.setCellValueFactory(new PropertyValueFactory<>("businessType"));
        operNameCol.setCellValueFactory(new PropertyValueFactory<>("operName"));
        operIpCol.setCellValueFactory(new PropertyValueFactory<>("operIp"));

        statusCol.setCellValueFactory(cb -> {
            var row = cb.getValue();
            var item = ObjectUtil.equal(0, row.getStatus());
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
                            state.setText("成功");
                            state.getStyleClass().addAll(BUTTON_OUTLINED, SUCCESS);
                        } else {
                            state.setText("失败");
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

        businessTypeCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button state = new Button();
                        SysDictData sysDictData = operlogViewModel.getSysDictDataMap().get(item + "");
                        if (sysDictData != null) {
                            state.setText(sysDictData.getDictLabel());
                            if (item == 3 || item == 7 || item == 9) {
                                state.getStyleClass().addAll(BUTTON_OUTLINED, DANGER);
                            } else if (item == 5 || item == 6 || item == 8) {
                                state.getStyleClass().addAll(BUTTON_OUTLINED, WARNING);
                            } else if (item == 4) {
                                state.getStyleClass().addAll(BUTTON_OUTLINED, ACCENT);
                            }
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

                        Button editBut = new Button("详细");
                        editBut.setOnAction(event -> showDictDataInfoDialog(getTableRow().getItem()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);

                        HBox box = new HBox(editBut);
                        box.setAlignment(Pos.CENTER);
//                            box.setSpacing(7);
                        setGraphic(box);
                    }
                }
            };
        });


        operTimeCol.setCellValueFactory(new PropertyValueFactory<>("operTime"));
        operTimeCol.setCellFactory(new Callback<TableColumn<SysOperLog, Date>, TableCell<SysOperLog, Date>>() {
            @Override
            public TableCell<SysOperLog, Date> call(TableColumn<SysOperLog, Date> param) {
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
        costTimeCol.setCellValueFactory(new PropertyValueFactory<>("costTime"));
        costTimeCol.setCellFactory(new Callback<TableColumn<SysOperLog, Long>, TableCell<SysOperLog, Long>>() {
            @Override
            public TableCell<SysOperLog, Long> call(TableColumn<SysOperLog, Long> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Long item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            if (item != null) {
                                this.setText(item + "毫秒");
                            }
                        }

                    }
                };
            }
        });
        tableView.setItems(operlogViewModel.getSysOperLogs());
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

    private void showDictDataInfoDialog(SysOperLog sysOperLog) {

        ViewTuple<OperLogInfoView, OperLogInfoViewModel> load = FluentViewLoader.fxmlView(OperLogInfoView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().setSysOperLog(sysOperLog);
        getDialogContent().addActions(Map.entry(new Button("关闭"), event -> dialog.close()));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("操作日志详细");
        getDialogContent().setContent(load.getView());
        getDialog().showDialog();
    }


    private void showDelDialog(List<Long> operLogIds) {

        if (CollUtil.isEmpty(operLogIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> operlogViewModel.del(CollUtil.join(operLogIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                operlogViewModel.updateData();
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
