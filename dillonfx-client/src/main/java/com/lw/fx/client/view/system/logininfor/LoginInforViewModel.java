package com.lw.fx.client.view.system.logininfor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lw.fx.client.domain.SysLogininfor;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysLogininforFeign;
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

public class LoginInforViewModel implements ViewModel {

    private ObservableList<SysLogininfor> sysDicts = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty ipaddr = new SimpleStringProperty();
    private StringProperty userName = new SimpleStringProperty();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public LoginInforViewModel() {
        queryLogininforList();
    }

    public void queryLogininforList() {


        Map<String, Object> querMap = new HashMap<>();
        if (ObjectUtil.isNotEmpty(startDate.getValue())) {
            querMap.put("params[beginTime]", startDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 00:00:00");

        }
        if (ObjectUtil.isNotEmpty(endDate.getValue())) {
            querMap.put("params[endTime]", endDate.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " 23:59:59");
        }

        querMap.put("ipaddr", ipaddr.getValue());
        querMap.put("userName", userName.getValue());
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysDicts.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysLogininforFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysLogininfor> logininfors = BeanUtil.copyToList(r.getRows(), SysLogininfor.class);
                    total.setValue(NumberUtil.parseInt(r.getTotal() + ""));

                    sysDicts.addAll(logininfors);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        ipaddr.setValue("");
        userName.setValue("");
        status.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysLogininfor> getSysLogininfors() {
        return sysDicts;
    }


    public void setSysLogininfors(ObservableList<SysLogininfor> sysDicts) {
        this.sysDicts = sysDicts;
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


    public String getIpaddr() {
        return ipaddr.get();
    }

    public StringProperty ipaddrProperty() {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr) {
        this.ipaddr.set(ipaddr);
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public void del(String logininforIds) {
        Request.connector(SysLogininforFeign.class).remove(logininforIds);
    }

    public void clean() {
        Request.connector(SysLogininforFeign.class).clean();
    }
}
