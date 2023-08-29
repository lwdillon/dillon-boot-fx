package com.lw.fx.client.view.system.operlog;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.domain.SysOperLog;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDictDataFeign;
import com.lw.fx.client.request.feign.client.SysOperlogFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OperLogViewModel implements ViewModel {

    private ObservableList<SysOperLog> sysOperLog = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty status = new SimpleStringProperty();
    private ObjectProperty<SysDictData> sysDictData = new SimpleObjectProperty<>();
    private StringProperty title = new SimpleStringProperty();
    private StringProperty operName = new SimpleStringProperty();

    private ObservableList<SysDictData> noticeDataObservableList = FXCollections.observableArrayList();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    private Map<String, SysDictData> sysDictDataMap = new HashMap<>();

    public OperLogViewModel() {
        initData();
    }

    private void initData() {
        ProcessChain.create().addRunnableInPlatformThread(() -> noticeDataObservableList.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysDictDataFeign.class).dictType("sys_oper_type")
                )
                .addConsumerInPlatformThread(r -> {

                    JSONObject objects = JSONUtil.parseObj(r.toString());
                    List<SysDictData> sysDictDataList = JSONUtil.toList(objects.getJSONArray("data"), SysDictData.class);
                    Map<String, SysDictData> stringSysDictDataMap = sysDictDataList.stream().collect(Collectors.toMap(SysDictData::getDictValue, Function.identity(), (key1, key2) -> key2));
                    setSysDictDataMap(stringSysDictDataMap);
                    noticeDataObservableList.setAll(sysDictDataList);
                })
                .addRunnableInExecutor(() -> updateData())
                .onException(e -> e.printStackTrace()).run();
    }

    public void updateData() {


        Map<String, Object> querMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(startDate.getValue())) {
            querMap.put("params[beginTime]", startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00");

        }
        if (ObjectUtil.isNotEmpty(endDate.getValue())) {
            querMap.put("params[endTime]", endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
        }

        querMap.put("title", title.getValue());
        querMap.put("operName", operName.getValue());
        if (ObjectUtil.isAllNotEmpty(sysDictData, sysDictData.getValue())) {
            querMap.put("businessType", sysDictData.getValue().getDictValue());
        }
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysOperLog.clear())

                .addSupplierInExecutor(() ->
                        Request.connector(SysOperlogFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysOperLog> sysOperLogs = BeanUtil.copyToList(r.getRows(), SysOperLog.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysOperLog.addAll(sysOperLogs);
                })

                .onException(e -> e.printStackTrace()).run();
    }

    public void reset() {
        title.setValue("");
        operName.setValue("");
        status.setValue("");
        sysDictData.setValue(null);
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysOperLog> getSysOperLogs() {
        return sysOperLog;
    }


    public void setSysOperLogs(ObservableList<SysOperLog> sysOperLog) {
        this.sysOperLog = sysOperLog;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }


    public int getTotal() {
        return total.get();
    }

    public SimpleIntegerProperty totalProperty() {
        return total;
    }

    public void setTotal(int total) {
        this.total.set(total);
    }

    public int getPageNum() {
        return pageNum.get();
    }

    public IntegerProperty pageNumProperty() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum.set(pageNum);
    }

    public long getPageSize() {
        return pageSize.get();
    }

    public IntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize.set(pageSize);
    }


    public SysDictData getSysDictData() {
        return sysDictData.get();
    }

    public ObjectProperty<SysDictData> sysDictDataProperty() {
        return sysDictData;
    }

    public void setSysDictData(SysDictData sysDictData) {
        this.sysDictData.set(sysDictData);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getOperName() {
        return operName.get();
    }

    public StringProperty operNameProperty() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName.set(operName);
    }

    public void del(String noticeIds) {
        Request.connector(SysOperlogFeign.class).remove(noticeIds);
    }

    public void clean() {
        Request.connector(SysOperlogFeign.class).clean();
    }

    public Map<String, SysDictData> getSysDictDataMap() {
        return sysDictDataMap;
    }

    public void setSysDictDataMap(Map<String, SysDictData> sysDictDataMap) {
        this.sysDictDataMap = sysDictDataMap;
    }

    public ObservableList<SysDictData> getDictDataObservableList() {
        return noticeDataObservableList;
    }

    public void setDictDataObservableList(ObservableList<SysDictData> noticeDataObservableList) {
        this.noticeDataObservableList = noticeDataObservableList;
    }
}
