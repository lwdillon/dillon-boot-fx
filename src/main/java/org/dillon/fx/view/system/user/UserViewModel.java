package org.dillon.fx.view.system.user;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dillon.fx.domain.AjaxResult;
import org.dillon.fx.domain.SysUser;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.domain.vo.TreeSelect;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysUserFeign;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserViewModel implements ViewModel {


    private final SimpleLongProperty deptSelectId = new SimpleLongProperty();
    private SimpleIntegerProperty total = new SimpleIntegerProperty(0);
    private ObservableList<TreeSelect> deptTreeList = FXCollections.observableArrayList();

    private ObservableList<SysUser> userList = FXCollections.observableArrayList();

    public void initialize() {
        deptSelectId.addListener((obs, old, val) -> userList());

    }

    public List<TreeSelect> getDeptTreeList() {
        return deptTreeList;
    }




    public ObservableList<SysUser> getUserList() {
        return userList;
    }


    public long getDeptSelectId() {
        return deptSelectId.get();
    }

    public SimpleLongProperty deptSelectIdProperty() {
        return deptSelectId;
    }

    public void setDeptSelectId(long deptSelectId) {
        this.deptSelectId.set(deptSelectId);
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
     * 部门树
     */
    public void userList() {
        userList.clear();
        setTotal(1);
        Map<String, Object> querMap = new HashMap<>();
        querMap.put("deptId", getDeptSelectId());
        TableDataInfo tableDataInfo = Request.connector(SysUserFeign.class).list(querMap);
        List<SysUser> users = BeanUtil.copyToList(tableDataInfo.getRows(), SysUser.class);
        setTotal(NumberUtil.parseInt(tableDataInfo.getTotal()+""));
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




}
