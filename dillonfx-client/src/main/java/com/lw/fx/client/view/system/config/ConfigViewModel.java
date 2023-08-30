package com.lw.fx.client.view.system.config;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysConfig;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysConfigFeign;
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

public class ConfigViewModel implements ViewModel {

    private ObservableList<SysConfig> sysConfigs = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty configType = new SimpleStringProperty();
    private StringProperty configName = new SimpleStringProperty();
    private StringProperty configKey = new SimpleStringProperty();
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public ConfigViewModel() {
        queryConfigDataList();
    }

    public void queryConfigDataList() {


        Map<String, Object> querMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(startDate.getValue())) {
            querMap.put("params[beginTime]", startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00");

        }
        if (ObjectUtil.isNotEmpty(endDate.getValue())) {
            querMap.put("params[endTime]", endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
        }

        querMap.put("configName", configName.getValue());
        querMap.put("configType", configType.getValue());
        querMap.put("configKey", configKey.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysConfigs.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysConfigFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysConfig> configs = BeanUtil.copyToList(r.getRows(), SysConfig.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysConfigs.addAll(configs);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        configName.setValue("");
        configType.setValue("");
        configKey.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysConfig> getSysConfigs() {
        return sysConfigs;
    }


    public void setSysConfigs(ObservableList<SysConfig> sysConfigs) {
        this.sysConfigs = sysConfigs;
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

    public String getConfigType() {
        return configType.get();
    }

    public StringProperty configTypeProperty() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType.set(configType);
    }

    public String getConfigName() {
        return configName.get();
    }

    public StringProperty configNameProperty() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName.set(configName);
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

    public void del(String configIds) {
        Request.connector(SysConfigFeign.class).remove(configIds);
    }

    public String getConfigKey() {
        return configKey.get();
    }

    public StringProperty configKeyProperty() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey.set(configKey);
    }
}
