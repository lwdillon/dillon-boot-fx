package com.lw.fx.client.view.system.role;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.domain.vo.TreeSelect;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysMenuFeign;
import com.lw.fx.client.request.feign.client.SysRoleFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import io.datafx.core.concurrent.ProcessChain;
import javafx.beans.property.*;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RoleInfoViewModel implements ViewModel {

    private ModelWrapper<SysRole> sysRoleWrapper = new ModelWrapper<>();

    private BooleanProperty selectAll = new SimpleBooleanProperty(false);
    private BooleanProperty expansionAll = new SimpleBooleanProperty(false);

    private ObjectProperty<CheckBoxTreeItem<TreeSelect>> rootItem = new SimpleObjectProperty<>(new CheckBoxTreeItem<>());

    public RoleInfoViewModel() {
        setSysRole(new SysRole());

    }

    /**
     * 设置系统角色
     *
     * @param sysRole 系统作用
     */
    public void setSysRole(SysRole sysRole) {
        this.sysRoleWrapper.set(sysRole);
        this.sysRoleWrapper.reload();
    }


    public IntegerProperty roleSortProperty() {
        return sysRoleWrapper.field("roleSort", SysRole::getRoleSort, SysRole::setRoleSort, 0);
    }

    public StringProperty roleKeyProperty() {
        return sysRoleWrapper.field("roleKey", SysRole::getRoleKey, SysRole::setRoleKey, "");
    }

    public StringProperty statusProperty() {
        return sysRoleWrapper.field("status", SysRole::getStatus, SysRole::setStatus, "0");
    }

    public StringProperty roleNameProperty() {
        return sysRoleWrapper.field("roleName", SysRole::getRoleName, SysRole::setRoleName, "");
    }

    public StringProperty remarkProperty() {
        return sysRoleWrapper.field("remark", SysRole::getRemark, SysRole::setRemark, "");
    }

    public CheckBoxTreeItem<TreeSelect> getRootItem() {
        return rootItem.get();
    }

    public ObjectProperty<CheckBoxTreeItem<TreeSelect>> rootItemProperty() {
        return rootItem;
    }

    public void setRootItem(CheckBoxTreeItem<TreeSelect> rootItem) {
        this.rootItem.set(rootItem);
    }

    public boolean isSelectAll() {
        return selectAll.get();
    }

    public BooleanProperty selectAllProperty() {
        return selectAll;
    }

    public void setSelectAll(boolean selectAll) {
        this.selectAll.set(selectAll);
    }

    public boolean isExpansionAll() {
        return expansionAll.get();
    }

    public BooleanProperty expansionAllProperty() {
        return expansionAll;
    }

    public void setExpansionAll(boolean expansionAll) {
        this.expansionAll.set(expansionAll);
    }

    /**
     * 保存
     *
     * @param isEdit 是编辑
     * @return {@link Boolean}
     */
    public Boolean save(boolean isEdit) {
        sysRoleWrapper.commit();
        SysRole sysRole = sysRoleWrapper.get();
        List<Long> menuIs = new ArrayList<>();
        getMenuId(rootItem.getValue(), menuIs);
        sysRole.setMenuIds(ArrayUtil.toArray(menuIs, Long.class));
        JsonObject result;
        if (isEdit) {
            result = Request.connector(SysRoleFeign.class).edit(sysRole);

        } else {
            result = Request.connector(SysRoleFeign.class).add(sysRole);
        }

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");
    }


    private CheckBoxTreeItem<TreeSelect> getMenuRootItem(Long roleId) {
        JsonObject ajaxResult = null;
        List<TreeSelect> menus = new ArrayList<>();
        List<Long> checkedKeys;
        if (ObjectUtil.isNull(roleId)) {
            checkedKeys = null;
            ajaxResult = Request.connector(SysMenuFeign.class).treeselect(new HashMap<>());
            JsonArray array = ajaxResult.getAsJsonArray(AjaxResult.DATA_TAG);
            menus = JSONUtil.toList(array.toString(), TreeSelect.class);
        } else {
            ajaxResult = Request.connector(SysMenuFeign.class).roleMenuTreeselect(roleId);
            JSONObject objects = JSONUtil.parseObj(ajaxResult.toString());
            menus = objects.getBeanList("menus", TreeSelect.class);
            checkedKeys = objects.getBeanList("checkedKeys", Long.class);

        }

        CheckBoxTreeItem<TreeSelect> rootItem = new CheckBoxTreeItem<TreeSelect>(new TreeSelect());
        rootItem.setExpanded(false);
        menus.forEach(obj -> {
            var child = new CheckBoxTreeItem<TreeSelect>(obj);
            if (ObjectUtil.isNotNull(checkedKeys) && CollUtil.contains(checkedKeys, obj.getId())) {
                child.setSelected(true);
            }
            var childObj = obj.getChildren();
            if (childObj != null) {
                generateTree(child, childObj, checkedKeys);
            }
            rootItem.getChildren().add(child);

        });
        return rootItem;
    }

    public void updateSysRoleInfo(Long sysRoleId) {

        //add
        if (ObjectUtil.isNull(sysRoleId)) {
            ProcessChain.create().addSupplierInExecutor(() -> getMenuRootItem(sysRoleId))
                    .addConsumerInPlatformThread(r -> {
                        rootItem.setValue(r);
                        setSysRole(new SysRole());
                    }).
                    onException(e -> e.printStackTrace()).run();
        } else {//edit
            ProcessChain.create().addSupplierInExecutor(() -> getSysRole(sysRoleId))
                    .addConsumerInPlatformThread(r -> setSysRole(r)).addSupplierInExecutor(() -> getMenuRootItem(sysRoleId))
                    .addConsumerInPlatformThread(r -> {
                        rootItem.setValue(r);
                    }).
                    onException(e -> e.printStackTrace()).run();
        }
    }

    private SysRole getSysRole(Long roleId) {
        JsonObject jsonObject = Request.connector(SysRoleFeign.class).getInfo(roleId);
        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        SysRole sysRole = objects.get(AjaxResult.DATA_TAG, SysRole.class);
        return sysRole;
    }

    private void generateTree(CheckBoxTreeItem<TreeSelect> parent, List<TreeSelect> children, List<Long> checkedKeys) {
        children.forEach(obj -> {

            var child = new CheckBoxTreeItem<TreeSelect>(obj);
            if (ObjectUtil.isNotNull(checkedKeys) && CollUtil.contains(checkedKeys, obj.getId())) {
                child.setSelected(true);
            }
            var childObj = obj.getChildren();
            if (childObj != null) {
                generateTree(child, childObj, checkedKeys);
            }
            parent.getChildren().add(child);

        });
    }

    private void getMenuId(TreeItem<TreeSelect> root, List<Long> menuIds) {

        for (TreeItem<TreeSelect> child : root.getChildren()) {

            if (child instanceof CheckBoxTreeItem<TreeSelect>) {
                if (((CheckBoxTreeItem<TreeSelect>) child).isSelected()) {
                    menuIds.add(child.getValue().getId());
                }
            }
            if (!child.getChildren().isEmpty()) {
                getMenuId(child, menuIds);
            }
        }
    }


}
