package com.lw.fx.client.view.system.dict.type;

import atlantafx.base.controls.RingProgressIndicator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysDictType;
import com.lw.fx.client.util.NodeUtils;
import com.lw.fx.client.view.control.PagingControl;
import com.lw.fx.client.view.control.WFXGenericDialog;
import com.lw.fx.client.view.system.dict.data.DictDataView;
import com.lw.fx.client.view.system.dict.data.DictDataViewModel;
import de.saxsys.mvvmfx.*;
import io.datafx.core.concurrent.ProcessChain;
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
import javafx.util.Callback;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class DictTypeView implements FxmlView<DictTypeViewModel>, Initializable {

    @InjectViewModel
    private DictTypeViewModel dictTypeViewModel;

    @FXML
    private VBox contentPane;
    @FXML
    private Button addBut;

    @FXML
    private TableColumn<SysDictType, Date> createTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button editBut;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TableColumn<SysDictType, String> optCol;


    @FXML
    private Button resetBut;

    @FXML
    private TableColumn<?, ?> dictNameCol;

    @FXML
    private TableColumn<?, ?> dictIdCol;
    @FXML
    private TableColumn<SysDictType, String> dictTypeCol;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysDictType, Boolean> selCol;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private TableColumn<SysDictType, Boolean> statusCol;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysDictType> tableView;

    @FXML
    private TextField dictNameField;


    private RingProgressIndicator loading;

    private WFXGenericDialog dialog;


    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(dictTypeViewModel.totalProperty());
        dictTypeViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        dictTypeViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        dictTypeViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            dictTypeViewModel.queryDictDataList();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            dictTypeViewModel.queryDictDataList();
        });
        loading = new RingProgressIndicator();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        dictNameField.textProperty().bindBidirectional(dictTypeViewModel.dictNameProperty());
        statusCombo.valueProperty().bindBidirectional(dictTypeViewModel.statusProperty());
        startDatePicker.valueProperty().bindBidirectional(dictTypeViewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(dictTypeViewModel.endDateProperty());
        searchBut.setOnAction(event -> dictTypeViewModel.queryDictDataList());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> dictTypeViewModel.reset());
        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showDictDataInfoDialog(tableView.getSelectionModel().getSelectedItem().getDictId());
        });
        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            dictTypeViewModel.getSysDictTypes().forEach(dict -> {
                if (dict.isSelect()) {
                    delIds.add(dict.getDictId());
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
                            setText(ObjectUtil.equal("0", item) ? "正常" : ObjectUtil.equal("1", item) ? "停用" : "全部");
                        }
                    }
                };
            }
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);
        dictNameCol.setCellValueFactory(new PropertyValueFactory<>("dictName"));
        dictTypeCol.setCellValueFactory(new PropertyValueFactory<>("dictType"));
        dictIdCol.setCellValueFactory(new PropertyValueFactory<>("dictId"));

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

        dictTypeCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Button editBut = new Button(item);
                        editBut.setOnAction(event -> {
                            ViewTuple<DictDataView, DictDataViewModel> load = FluentViewLoader.fxmlView(DictDataView.class).load();
                            load.getViewModel().queryDictDataList(getTableRow().getItem());
                            MvvmFX.getNotificationCenter().publish("addTab", "字典数据", "", load.getView());
                        });
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        HBox box = new HBox(editBut);
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
                        editBut.setOnAction(event -> showDictDataInfoDialog(getTableRow().getItem().getDictId()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(CollUtil.newArrayList(getTableRow().getItem().getDictId())));
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
        createTimeCol.setCellFactory(new Callback<TableColumn<SysDictType, Date>, TableCell<SysDictType, Date>>() {
            @Override
            public TableCell<SysDictType, Date> call(TableColumn<SysDictType, Date> param) {
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
        tableView.setItems(dictTypeViewModel.getSysDictTypes());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        addBut.setOnAction(event -> showDictDataInfoDialog(null));

    }

    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();
        }
        return dialog;
    }


    private void showDictDataInfoDialog(Long dictTypeId) {

        ViewTuple<DictTypeInfoView, DictTypeInfoViewModel> load = FluentViewLoader.fxmlView(DictTypeInfoView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().updateSysDictTypeInfo(dictTypeId);
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(dictTypeId))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    dictTypeViewModel.queryDictDataList();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(dictTypeId) ? "编辑字典类型" : "添加字典类型");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());
    }


    private void showDelDialog(List<Long> dictIds) {

        if (CollUtil.isEmpty(dictIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> dictTypeViewModel.del(CollUtil.join(dictIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                dictTypeViewModel.queryDictDataList();
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + dictIds + "的字典类型吗？"));
        getDialogContent().show(rootPane.getScene());
    }


}
