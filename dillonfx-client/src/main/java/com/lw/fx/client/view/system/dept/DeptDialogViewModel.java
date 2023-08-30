package com.lw.fx.client.view.system.dept;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysDept;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysDeptFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;

/**
 * 部门对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class DeptDialogViewModel implements ViewModel, SceneLifecycle {
    public final static String ON_CLOSE = "close";
    private ObservableList<SysDept> allDeptData = FXCollections.observableArrayList();

    /**
     * 包装器
     */
    private ModelWrapper<SysDept> wrapper = new ModelWrapper<>();


    private ObjectProperty<SysDept> selectSysDept = new SimpleObjectProperty<>(new SysDept());


    public void initialize() {


    }


    /**
     * 系统设置部门
     *
     * @param SysDept 系统部门
     */
    public void setSysDept(SysDept SysDept) {
        wrapper.set(SysDept);
        wrapper.reload();
    }

    /**
     * 父id属性
     *
     * @return {@link LongProperty}
     */
    public LongProperty parentIdProperty() {
        return wrapper.field("parentId", SysDept::getParentId, SysDept::setParentId, 0L);
    }


    /**
     * 部门名称属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty deptNameProperty() {
        return wrapper.field("deptName", SysDept::getDeptName, SysDept::setDeptName, "");
    }

    public StringProperty leaderProperty() {
        return wrapper.field("leader", SysDept::getLeader, SysDept::setLeader, "");
    }

    public StringProperty phoneProperty() {
        return wrapper.field("phone", SysDept::getPhone, SysDept::setPhone, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", SysDept::getEmail, SysDept::setEmail, "");
    }


    /**
     * 显示顺序属性
     *
     * @return {@link IntegerProperty}
     */
    public IntegerProperty orderNumProperty() {
        return wrapper.field("orderNum", SysDept::getOrderNum, SysDept::setOrderNum, 0);
    }


    /**
     * 状态属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty statusProperty() {
        return wrapper.field("status", SysDept::getStatus, SysDept::setStatus, "0");
    }

    public SysDept getSelectSysDept() {
        return selectSysDept.get();
    }

    public ObjectProperty<SysDept> selectSysDeptProperty() {
        return selectSysDept;
    }

    public void setSelectSysDept(SysDept selectSysDept) {
        this.selectSysDept.set(selectSysDept);
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }

    public ObservableList<SysDept> getAllDeptData() {
        return allDeptData;
    }


    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        wrapper.commit();
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysDeptFeign.class).edit(wrapper.get());
        } else {
            result = Request.connector(SysDeptFeign.class).add(wrapper.get());
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }

    /**
     * 部门列表
     */
    public void deptList() {
        JsonObject routers = Request.connector(SysDeptFeign.class).list(new HashMap<>());

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);

        JSONArray objects = JSONUtil.parseArray(array.toString());
        List<SysDept> depts = JSONUtil.toList(objects, SysDept.class);
        allDeptData.addAll(depts);
    }
}
