package com.lw.fx.client.view.system.role;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.domain.SysUserRole;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysRoleFeign;
import de.saxsys.mvvmfx.ViewModel;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthUserViewModel implements ViewModel {


    private StringProperty userName = new SimpleStringProperty();
    private StringProperty phone = new SimpleStringProperty();

    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private IntegerProperty pageNum = new SimpleIntegerProperty(0);
    private IntegerProperty pageSize = new SimpleIntegerProperty(10);
    private ObservableList<SysUser> userList = FXCollections.observableArrayList();

    private Long roleId;

    public void initialize() {
    }

    public ObservableList<SysUser> getUserList() {
        return userList;
    }


    public void reset() {
        userName.setValue("");
        phone.setValue("");
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


    public void selectAll(boolean sel) {

        for (SysUser sysUser : getUserList()) {
            sysUser.setSelect(sel);
        }
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

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    public void allocatedList() {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", getRoleId());
        map.put("userName", userName.getValue());
        map.put("phonenumber", phone.getValue());
        map.put("pageNum", pageNum.getValue() + 1);
        map.put("pageSize", pageSize.getValue());
        ProcessChain.create().addSupplierInExecutor(() -> {
                    TableDataInfo tableDataInfo = Request.connector(SysRoleFeign.class).allocatedList(map);
                    List<SysUser> sysUserList = BeanUtil.copyToList(tableDataInfo.getRows(), SysUser.class);
                    return sysUserList;
                })
                .addConsumerInPlatformThread(r -> {
                    userList.clear();
                    userList.setAll(r);
                    setTotal(r.size());

                })
                .onException(e -> e.printStackTrace()).run();
    }

    public Boolean cancel(SysUserRole userRole) {

        JsonObject result = Request.connector(SysRoleFeign.class).cancelAuthUser(userRole);
        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }

    public Boolean cancelAll(Long roleId, String userIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("roleId", roleId);
        map.put("userIds", userIds);
        JsonObject result = Request.connector(SysRoleFeign.class).cancelAuthUserAll(map);
        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
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

    public int getPageSize() {
        return pageSize.get();
    }

    public IntegerProperty pageSizeProperty() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize.set(pageSize);
    }
}
