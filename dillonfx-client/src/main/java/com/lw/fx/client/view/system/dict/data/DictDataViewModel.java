package com.lw.fx.client.view.system.dict.data;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lw.fx.client.domain.SysDictData;
import com.lw.fx.client.domain.SysDictType;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDictDataFeign;
import com.lw.fx.client.request.feign.client.SysDictTypeFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictDataViewModel implements ViewModel {

    private ObservableList<SysDictData> sysDictDatas = FXCollections.observableArrayList();
    private ObservableList<SysDictType> dictTypes = FXCollections.observableArrayList();

    private long dictTypeId;
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty dictLabel = new SimpleStringProperty("");
    private ObjectProperty<SysDictType> selectDictType = new SimpleObjectProperty<>();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public DictDataViewModel() {
    }

    public void updateData() {

        Map<String, Object> querMap = new HashMap<>();
        querMap.put("dictLabel", dictLabel.getValue());
        querMap.put("dictType", selectDictType.getValue().getDictType());
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysDictDatas.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysDictDataFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysDictData> dicts = BeanUtil.copyToList(r.getRows(), SysDictData.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysDictDatas.addAll(dicts);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void queryDictDataList(SysDictType sysDictType) {

        Map<String, Object> querMap = new HashMap<>();
        querMap.put("dictType", sysDictType.getDictType());
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> {
                    dictTypes.clear();
                    sysDictDatas.clear();
                }).addSupplierInExecutor(() -> Request.connector(SysDictTypeFeign.class).optionselect())
                .addConsumerInPlatformThread(rel -> {

                    JSONObject objects = JSONUtil.parseObj(rel.toString());
                    List<SysDictType> sysDictTypeList = JSONUtil.toList(objects.getJSONArray("data"), SysDictType.class);
                    dictTypes.setAll(sysDictTypeList);
                    SysDictType select = CollUtil.findOne(sysDictTypeList, item -> ObjectUtil.equals(item.getDictName(), sysDictType.getDictName()));
                    setSelectDictType(select);

                })
                .addSupplierInExecutor(() ->
                        Request.connector(SysDictDataFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysDictData> dicts = BeanUtil.copyToList(r.getRows(), SysDictData.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysDictDatas.addAll(dicts);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        dictLabel.setValue("");
        status.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysDictData> getSysDictDatas() {
        return sysDictDatas;
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

    public String getDictLabel() {
        return dictLabel.get();
    }

    public StringProperty dictLabelProperty() {
        return dictLabel;
    }

    public void setDictLabel(String dictLabel) {
        this.dictLabel.set(dictLabel);
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

    public void del(String dictIds) {
        Request.connector(SysDictDataFeign.class).remove(dictIds);
    }

    public ObservableList<SysDictType> getDictTypes() {
        return dictTypes;
    }

    public void setDictTypes(ObservableList<SysDictType> dictTypes) {
        this.dictTypes = dictTypes;
    }

    public long getDictTypeId() {
        return dictTypeId;
    }

    public void setDictTypeId(long dictTypeId) {
        this.dictTypeId = dictTypeId;
    }

    public SysDictType getSelectDictType() {
        return selectDictType.get();
    }

    public ObjectProperty<SysDictType> selectDictTypeProperty() {
        return selectDictType;
    }

    public void setSelectDictType(SysDictType selectDictType) {
        this.selectDictType.set(selectDictType);
    }
}
