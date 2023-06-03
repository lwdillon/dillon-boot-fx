package org.dillon.fx.view.system.role;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dillon.fx.domain.SysRole;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysRoleFeign;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleViewModel implements ViewModel {

    private ObservableList<SysRole> sysRoles = FXCollections.observableArrayList();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty roleName = new SimpleStringProperty();

    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);

    public RoleViewModel() {
        queryRoleList();
    }

    public void queryRoleList() {

        Map<String, Object> params = new HashMap<>();
        if (ObjectUtil.isNotEmpty(startDate.getValue())) {
            params.put("beginTime", new Date());

        }
        if (ObjectUtil.isNotEmpty(endDate.getValue())) {
            params.put("endTime", new Date());

        }
        Map<String, Object> querMap = new HashMap<>();
        querMap.put("roleName", roleName.getValue());
        querMap.put("status", status.getValue());
        querMap.put("pageNum", pageNum.getValue() + 1);
        querMap.put("pageSize", pageSize.getValue());

        ProcessChain.create().addRunnableInPlatformThread(() -> sysRoles.clear())
                .addSupplierInExecutor(() ->
                        Request.connector(SysRoleFeign.class).list(querMap)
                )
                .addConsumerInPlatformThread(r -> {
                    List<SysRole> roles = BeanUtil.copyToList(r.getRows(), SysRole.class);
                    setTotal(NumberUtil.parseInt(r.getTotal() + ""));

                    sysRoles.addAll(roles);
                })

                .onException(e -> e.printStackTrace()).run();


    }

    public void reset() {
        roleName.setValue("");
        status.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
    }

    public ObservableList<SysRole> getSysRoles() {
        return sysRoles;
    }


    public void setSysRoles(ObservableList<SysRole> sysRoles) {
        this.sysRoles = sysRoles;
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

    public String getRoleName() {
        return roleName.get();
    }

    public StringProperty roleNameProperty() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName.set(roleName);
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

    public void del(String roleIds) {
        Request.connector(SysRoleFeign.class).remove(roleIds);
    }
}
