package com.lw.fx.client.view.system.notice;

import atlantafx.base.controls.RingProgressIndicator;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.domain.SysNotice;
import com.lw.fx.client.util.NodeUtils;
import com.lw.fx.client.view.control.PagingControl;
import com.lw.fx.client.view.control.WFXGenericDialog;
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
import org.kordamp.ikonli.material2.Material2AL;

import java.net.URL;
import java.util.*;

import static atlantafx.base.theme.Styles.*;
import static atlantafx.base.theme.Tweaks.*;

public class NoticeView implements FxmlView<NoticeViewModel>, Initializable {

    @InjectViewModel
    private NoticeViewModel noticeViewModel;

    @FXML
    private VBox contentPane;
    @FXML
    private Button addBut;

    @FXML
    private Button delBut;

    @FXML
    private Button editBut;

    @FXML
    private Button resetBut;

    @FXML
    private TableColumn<SysNotice, Date> createTimeCol;

    @FXML
    private TableColumn<SysNotice, String> optCol;

    @FXML
    private TableColumn<?, ?> noticeIdCol;
    @FXML
    private TableColumn<?, ?> noticeTitleCol;
    @FXML
    private TableColumn<?, ?> createByCol;
    @FXML
    private TableColumn<SysNotice, String> noticeTypeCol;
    @FXML
    private TableColumn<SysNotice, Boolean> selCol;

    @FXML
    private TableColumn<SysNotice, Boolean> statusCol;

    @FXML
    private StackPane rootPane;

    @FXML
    private Button searchBut;

    @FXML
    private CheckBox selAllCheckBox;

    @FXML
    private ComboBox<SysDictData> noticeTypeCombo;

    @FXML
    private TableView<SysNotice> tableView;

    @FXML
    private TextField noticeTitleField;
    @FXML
    private TextField createByField;

    private RingProgressIndicator loading;

    private WFXGenericDialog dialog;


    private PagingControl pagingControl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        pagingControl = new PagingControl();
        contentPane.getChildren().add(pagingControl);
        pagingControl.totalProperty().bindBidirectional(noticeViewModel.totalProperty());
        noticeViewModel.pageNumProperty().bind(pagingControl.pageNumProperty());
        noticeViewModel.pageSizeProperty().bindBidirectional(pagingControl.pageSizeProperty());
        noticeViewModel.pageNumProperty().addListener((observable, oldValue, newValue) -> {
            noticeViewModel.updateData();
        });
        pagingControl.pageSizeProperty().addListener((observable, oldValue, newValue) -> {
            noticeViewModel.updateData();
        });
        loading = new RingProgressIndicator();
        loading.disableProperty().bind(loading.visibleProperty().not());
        loading.visibleProperty().bindBidirectional(contentPane.disableProperty());
        rootPane.getChildren().add(loading);

        noticeTitleField.textProperty().bindBidirectional(noticeViewModel.noticeTitleProperty());
        createByField.textProperty().bindBidirectional(noticeViewModel.createByProperty());
        noticeTypeCombo.valueProperty().bindBidirectional(noticeViewModel.noticeTypeProperty());
        noticeTypeCombo.setItems(noticeViewModel.getNoticeDataObservableList());
        searchBut.setOnAction(event -> noticeViewModel.updateData());
        searchBut.getStyleClass().addAll(ACCENT);

        resetBut.setOnAction(event -> noticeViewModel.reset());
        editBut.setOnAction(event -> {
            if (tableView.getSelectionModel().getSelectedItem() == null) {
                MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
                return;
            }
            showDictDataInfoDialog(tableView.getSelectionModel().getSelectedItem().getNoticeId(), noticeViewModel.getSysDictDataMap().get(tableView.getSelectionModel().getSelectedItem().getNoticeType()));
        });
        delBut.setOnAction(event -> {
            List<Long> delIds = new ArrayList<>();
            noticeViewModel.getSysNotices().forEach(notice -> {
                if (notice.isSelect()) {
                    delIds.add(notice.getNoticeId());
                }
            });
            showDelDialog(delIds);
        });
        noticeTypeCombo.setCellFactory(new Callback<ListView<SysDictData>, ListCell<SysDictData>>() {
            @Override
            public ListCell<SysDictData> call(ListView<SysDictData> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(SysDictData item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(ObjectUtil.equal("0", item) ? "正常" : ObjectUtil.equal("1", item) ? "关闭" : "全部");
                        }
                    }
                };
            }
        });

        selCol.setCellValueFactory(new PropertyValueFactory<>("select"));
        selCol.setCellFactory(CheckBoxTableCell.forTableColumn(selCol));
        selCol.setEditable(true);

        noticeIdCol.setCellValueFactory(new PropertyValueFactory<>("noticeId"));
        noticeTitleCol.setCellValueFactory(new PropertyValueFactory<>("noticeTitle"));
        noticeTypeCol.setCellValueFactory(new PropertyValueFactory<>("noticeType"));
        createByCol.setCellValueFactory(new PropertyValueFactory<>("createBy"));

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
                        Label state = new Label("", new FontIcon(Material2AL.LABEL));
                        if (item) {
                            state.setText("正常");
                            state.getStyleClass().addAll(SUCCESS);
                        } else {
                            state.setText("关闭");
                            state.getStyleClass().addAll(DANGER);
                        }
                        HBox box = new HBox(state);
                        box.setPadding(new Insets(7, 7, 7, 7));
                        box.setAlignment(Pos.CENTER);
                        setGraphic(box);
                    }
                }
            };
        });

        noticeTypeCol.setCellFactory(col -> {
            return new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        Label state = new Label("", new FontIcon(Material2AL.LABEL));
                        SysDictData sysDictData = noticeViewModel.getSysDictDataMap().get(item);
                        if (sysDictData != null) {
                            state.setText(sysDictData.getDictLabel());
                            if (StrUtil.equals(sysDictData.getDictValue(), "1")) {
                                state.getStyleClass().addAll(SUCCESS);
                            } else if (StrUtil.equals(sysDictData.getDictValue(), "2")) {
                                state.getStyleClass().addAll(WARNING);
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

                        Button editBut = new Button("修改");
                        editBut.setOnAction(event -> showDictDataInfoDialog(getTableRow().getItem().getNoticeId(), noticeViewModel.getSysDictDataMap().get(getTableRow().getItem().getNoticeType())));
                        editBut.setGraphic(FontIcon.of(Feather.EDIT));
                        editBut.getStyleClass().addAll(FLAT, ACCENT);
                        Button remBut = new Button("删除");
                        remBut.setOnAction(event -> showDelDialog(CollUtil.newArrayList(getTableRow().getItem().getNoticeId())));
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
        createTimeCol.setCellFactory(new Callback<TableColumn<SysNotice, Date>, TableCell<SysNotice, Date>>() {
            @Override
            public TableCell<SysNotice, Date> call(TableColumn<SysNotice, Date> param) {
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
        tableView.setItems(noticeViewModel.getSysNotices());
        tableView.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<?, ?> c : tableView.getColumns()) {
            NodeUtils.addStyleClass(c, ALIGN_CENTER, ALIGN_LEFT, ALIGN_RIGHT);
        }


        addBut.setOnAction(event -> showDictDataInfoDialog(null, null));

    }

    public WFXGenericDialog getDialogContent() {
        if (dialog == null) {
            dialog = new WFXGenericDialog();
        }
        return dialog;
    }


    private void showDictDataInfoDialog(Long noticeTypeId, SysDictData sysDictData) {

        ViewTuple<NoticeInfoView, NoticeInfoViewModel> load = FluentViewLoader.fxmlView(NoticeInfoView.class).load();
        getDialogContent().clearActions();
        load.getViewModel().setNoticeDataObservableList(noticeViewModel.getNoticeDataObservableList());
        load.getViewModel().updateSysNoticeInfo(noticeTypeId, sysDictData);
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInPlatformThread(() -> load.getViewModel().commitHtmlText()).addSupplierInExecutor(
                            () -> load.getViewModel().save(ObjectUtil.isNotEmpty(noticeTypeId)))
                    .addConsumerInPlatformThread(r -> {
                        if (r) {
                            dialog.close();
                            noticeViewModel.updateData();
                        }
                    }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText(ObjectUtil.isNotEmpty(noticeTypeId) ? "编辑公告" : "添加公告");
        getDialogContent().setContent(load.getView());
        getDialogContent().show(rootPane.getScene());
    }


    private void showDelDialog(List<Long> noticeIds) {

        if (CollUtil.isEmpty(noticeIds)) {
            MvvmFX.getNotificationCenter().publish("message", 500, "请选择一条记录");
            return;
        }
        getDialogContent().clearActions();
        getDialogContent().addActions(Map.entry(new Button("取消"), event -> dialog.close()), Map.entry(new Button("确定"), event -> {
            ProcessChain.create().addRunnableInExecutor(() -> noticeViewModel.del(CollUtil.join(noticeIds, ","))).addRunnableInPlatformThread(() -> {
                dialog.close();
                noticeViewModel.updateData();
            }).onException(e -> e.printStackTrace()).run();
        }));


        getDialogContent().setHeaderIcon(FontIcon.of(Feather.INFO));
        getDialogContent().setHeaderText("系统揭示");
        getDialogContent().setContent(new Label("是否确认删除编号为" + noticeIds + "的公告吗？"));
        getDialogContent().show(rootPane.getScene());
    }


}
