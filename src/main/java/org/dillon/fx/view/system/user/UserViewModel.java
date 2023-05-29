package org.dillon.fx.view.system.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dillon.fx.domain.AjaxResult;
import org.dillon.fx.domain.SysUser;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.domain.vo.TreeSelect;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysMenuFeign;
import org.dillon.fx.request.feign.client.SysUserFeign;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel implements ViewModel {


    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> endDate = new SimpleObjectProperty();
    private ObservableList<TreeSelect> deptTreeList = FXCollections.observableArrayList();
    private ObservableList<SysUser> userList = FXCollections.observableArrayList();
    private StringProperty status = new SimpleStringProperty();
    private StringProperty userName = new SimpleStringProperty();
    private LongProperty deptId = new SimpleLongProperty();

    public void initialize() {
        deptId.addListener((obs, old, val) -> userList());

    }


    public ObservableList<TreeSelect> getDeptTreeList() {
        return deptTreeList;
    }

    public ObservableList<SysUser> getUserList() {
        return userList;
    }


    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public String getUserName() {
        return userName.get();
    }

    public StringProperty userNameProperty() {
        return userName;
    }

    public long getDeptId() {
        return deptId.get();
    }

    public LongProperty deptIdProperty() {
        return deptId;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }


    public void  reset(){
        userName.setValue("");
        status.setValue("");
        endDate.setValue(null);
        startDate.setValue(null);
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


    /**
     * 用户列表
     */
    public void userList() {
        userList.clear();
        setTotal(1);

        Map<String, Object> params = new HashMap<>();
        if (ObjectUtil.isNotEmpty(startDate.getValue())) {
            params.put("beginTime", new Date());

        }
        if (ObjectUtil.isNotEmpty(endDate.getValue())) {
            params.put("endTime",new Date());

        }
        Map<String, Object> querMap = new HashMap<>();
        querMap.put("userName", userName.getValue());
        querMap.put("status", status.getValue());
        querMap.put("deptId", deptId.getValue());

//        if (ObjectUtil.isNotEmpty(params)) {
//            querMap.put("params", JSONUtil.toJsonStr(params));
//        }


        TableDataInfo tableDataInfo = Request.connector(SysUserFeign.class).list(querMap);
        List<SysUser> users = BeanUtil.copyToList(tableDataInfo.getRows(), SysUser.class);
        setTotal(NumberUtil.parseInt(tableDataInfo.getTotal() + ""));
        userList.addAll(users);

    }

    /**
     * 部门树
     */
    public void deptTree() {
        deptTreeList.clear();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("deptName", "");
        JsonObject resObj = Request.connector(SysUserFeign.class).deptTree(queryMap);
        JsonArray array = resObj.getAsJsonArray(AjaxResult.DATA_TAG);
        List<TreeSelect> sysMenuList = JSONUtil.toList(array.toString(), TreeSelect.class);
        deptTreeList.addAll(sysMenuList);
    }


    public void remove(String menuId) {
        Request.connector(SysUserFeign.class).remove(menuId);

    }

    public void restPassword(SysUser user){
        Request.connector(SysUserFeign.class).resetPwd(user);

    }

    public void selectAll(boolean sel){

        for (SysUser sysUser : getUserList()) {
            sysUser.setSelect(sel);
        }
    }

}
