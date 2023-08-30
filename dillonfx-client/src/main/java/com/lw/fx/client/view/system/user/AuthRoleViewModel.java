package com.lw.fx.client.view.system.user;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysUserFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthRoleViewModel implements ViewModel {

    private ModelWrapper<SysUser> wrapper = new ModelWrapper<>();

    private ObservableList<SysRole> roles = FXCollections.observableArrayList();

    private Long userId = null;

    private Command getAuthRoleCommand;

    public void initialize() {
        setSysUser(new SysUser());
        getAuthRoleCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                getAuthRole();
            }
        }, true); //Async
    }

    private void getAuthRole() {
        JsonObject jsonObject = Request.connector(SysUserFeign.class).authRole(userId);

        JSONObject objects = JSONUtil.parseObj(jsonObject.toString());
        List<SysRole> roles = JSONUtil.toList(objects.getJSONArray("roles"), SysRole.class);
        SysUser sysUser = JSONUtil.toBean(objects.getStr("user"), SysUser.class);
        Platform.runLater(() -> {
            setSysUser(sysUser);
            this.roles.clear();
            this.roles.addAll(roles);
        });

    }

    public void setSysUser(SysUser sysUser) {
        wrapper.set(sysUser);
        wrapper.reload();
    }

    public StringProperty userNameProperty() {
        return wrapper.field("userName", SysUser::getUserName, SysUser::setUserName, "");
    }

    public StringProperty nickNameProperty() {
        return wrapper.field("nickName", SysUser::getNickName, SysUser::setNickName, "");
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        getAuthRoleCommand.execute();
    }

    public Command getGetAuthRoleCommand() {
        return getAuthRoleCommand;
    }

    public ObservableList<SysRole> getRoles() {
        return roles;
    }

    public void selectAll(boolean sel) {

        for (SysRole sysRole : getRoles()) {
            sysRole.setFlag(sel);
        }
    }

    public boolean save() {
        List<Long> roleIds = new ArrayList<>();
        for (SysRole sysRole : getRoles()) {
            if (sysRole.isSelect()) {
                roleIds.add(sysRole.getRoleId());
            }
        }
        Map<String, Object> putMap = new HashMap<>();
        putMap.put("userId", userId);
        putMap.put("roleIds", CollUtil.join(roleIds, ","));

        JsonObject result = Request.connector(SysUserFeign.class).insertAuthRole(putMap);
        ;

        return ObjectUtil.equals(result.get(AjaxResult.CODE_TAG).getAsString(), "200");

    }
}
