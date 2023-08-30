package com.lw.fx.client.view.config;

import cn.hutool.core.bean.BeanUtil;
import com.lw.fx.client.domain.SysDept;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysUserFeign;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;

public class UserInfoViewModel implements ViewModel {

    private ModelWrapper<SysUser> wrapper = new ModelWrapper<>();
    private StringProperty roles = new SimpleStringProperty();
    private StringProperty dept = new SimpleStringProperty();
    private Command command;

    public UserInfoViewModel() {
        command = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                getUserInfo();
            }
        }, true); //Async
        command.execute();
    }

    public void setPerson(SysUser sysUser) {
        wrapper.set(sysUser);
        wrapper.reload();
    }


    public StringProperty userNameProperty() {
        return wrapper.field("userName", SysUser::getUserName, SysUser::setUserName, "");
    }

    public StringProperty phonenumberProperty() {
        return wrapper.field("phonenumber", SysUser::getPhonenumber, SysUser::setPhonenumber, "");
    }

    public StringProperty emailProperty() {
        return wrapper.field("email", SysUser::getEmail, SysUser::setEmail, "");
    }


    public ObjectProperty createTimeProperty() {
        return wrapper.field("createTime", SysUser::getCreateTime, SysUser::setCreateTime, null);
    }

    public ObjectProperty<SysDept> deptProperty() {
        return wrapper.field("dept", SysUser::getDept, SysUser::setDept, null);
    }

    public String getRoles() {
        return roles.get();
    }

    public StringProperty rolesProperty() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles.set(roles);
    }

    private void getUserInfo() {
        Map<String, Object> ajaxResult = Request.connector(SysUserFeign.class).getInfo();
        Map map = (Map) ajaxResult.get("user");
        SysUser sysUser = BeanUtil.toBeanIgnoreCase(map, SysUser.class, true);
        Platform.runLater(() -> {
            setPerson(sysUser);
            roles.setValue(ajaxResult.get("roles") + "");
        });

    }

}
