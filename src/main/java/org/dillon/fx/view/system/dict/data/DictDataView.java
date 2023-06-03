package org.dillon.fx.view.system.dict.data;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.*;
import de.saxsys.mvvmfx.internal.viewloader.View;
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
import org.dillon.fx.theme.CSSFragment;
import org.dillon.fx.view.control.OverlayDialog;
import org.dillon.fx.view.control.PagingControl;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class DictDataView implements FxmlView<DictDataViewModel>, Initializable {

    @InjectViewModel
    private DictDataViewModel dictDataViewModel;

    @FXML
    private VBox contentPane;
    @FXML
    private Button addBut;

    @FXML
    private TableColumn<SysDictData, Date> createTimeCol;

    @FXML
    private Button delBut;

    @FXML
    private Button editBut;


    @FXML
    private ComboBox<SysDictType> dictNameCombo;

    @FXML
    private TableColumn<SysDictData, String> optCol;


    @FXML
    private Button resetBut;

    @FXML
    private TableColumn<?, ?> dictCodeCol;

    @FXML
    private TableColumn<?, ?> dictLabelCol;

    @FXML
    private TableColumn<?, ?> dictValueCol;
    @FXML
    private TableColumn<?, ?> remarkCol;

    @FXML
    private TableColumn<?, ?> dictSortCol;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private TableColumn<SysDictData, Boolean> selCol;

    @FXML
    private TableColumn<SysDictData, Boolean> statusCol;

    @FXML
    private ComboBox<String> statusCombo;

    @FXML
    private TableView<SysDictData> tableView;

    @FXML
    private TextField dictLabelField;

    private MFXProgressSpinner loading;

    private MFXStageDialog dialog;

    private MFXGenericDialog dialogContent;

    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(dictDataViewModel.totalProperty());
        dictDataViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        dictDataViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        dictDataViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            dictDataViewModel.updateData();
        });

        loading = new MFXProgressSpinner();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        dictLabelField.textProperty().bindBidirectional(dictDataViewModel.dictLabelProperty());
        statusCombo.valueProperty().bindBidirectional(dictDataViewModel.statusProperty());
        searchBut.setOnAction(event -> dictDataViewModel.updateData());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> dictDataViewModel.reset());
        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showDictDataInfoDialog(tableView.getSelectionModel().getSelectedItem().getDictCode(),tableView.getSelectionModel().getSelectedItem().getDictType());
        });
        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            dictDataViewModel.getSysDictDatas().forEach(dict -> {
                if (dict.isSelect()) {
                    delIds.add(dict.getDictCode());
                }
            });
            showDelDialog(delIds);
        });
        dictNameCombo.setItems(dictDataViewModel.getDictTypes());
        dictNameCombo.setCellFactory(new Callback<ListView<SysDictType>, ListCell<SysDictType>>() {
            @Override
            public ListCell<SysDictType> call(ListView<SysDictType> param) {
                final ListCell<SysDictType> cell = new ListCell<SysDictType>() {

                    @Override
                    public void updateItem(SysDictType item,
                                           boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getDictName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
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
        dictCodeCol.setCellValueFactory(new PropertyValueFactory<>("dictCode"));
        dictLabelCol.setCellValueFactory(new PropertyValueFactory<>("dictLabel"));
        dictValueCol.setCellValueFactory(new PropertyValueFactory<>("dictValue"));
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));
        dictSortCol.setCellValueFactory(new PropertyValueFactory<>("dictSort"));

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
                        editBut.setOnAction(event -> showDictDataInfoDialog(getTableRow().getItem().getDictCode(),getTableRow().getItem().getDictType()));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(CollUtil.newArrayList(getTableRow().getItem().getDictCode())));
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
        createTimeCol.setCellFactory(new Callback<TableColumn<SysDictData, Date>, TableCell<SysDictData, Date>>() {
            @Override
            public TableCell<SysDictData, Date> call(TableColumn<SysDictData, Date> param) {
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
        tableView.setItems(dictDataViewModel.getSysDictDatas());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        addBut.setOnAction(event -> showDictDataInfoDialog(null,dictDataViewModel.getSelectDictType().getDictType()));

        dictDataViewModel.selectDictTypeProperty().addListener((observable, oldValue, newValue) -> {
            dictNameCombo.getSelectionModel().select(newValue);
        });

        dictNameCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> dictDataViewModel.setSelectDictType(newValue));

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

    private void showDictDataInfoDialog(Long userId,String dictType) {

        ViewTuple<DictDataInfoView, DictDataInfoViewModel> load = FluentViewLoader.fxmlView(DictDataInfoView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().updateSysDictDataInfo(userId,dictType);
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addSupplierInExecutor(() -> load.getViewModel().save(ObjectUtil.isNotEmpty(userId))).addConsumerInPlatformThread(r -> {
                if (r) {
                    dialog.close();
                    dictDataViewModel.updateData();
                }
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(userId) ? "编辑字典数据" : "添加字典数据");
        getDialogContent().setContent(load.getView());
        getDialog().showDialog();
    }


    private void showDelDialog(List<Long> dictIds) {

        if (CollUtil.isEmpty(dictIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> dictDataViewModel.del(CollUtil.join(dictIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                dictDataViewModel.updateData();
            }).onException(e -> e.printStackTrace()).run();
        }));
        getDialogContent().setShowAlwaysOnTop(false);
        getDialogContent().setShowMinimize(false);

        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + dictIds + "的字典数据吗？"));
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
