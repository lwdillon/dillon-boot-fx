package org.dillon.fx.view.system.user;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import javafx.beans.property.StringProperty;
import org.dillon.fx.domain.SysUser;

public class UserInfoViewModel implements ViewModel {

    /**
     * 包装器
     */
    private ModelWrapper<SysUser> wrapper = new ModelWrapper<>();

    public void initialize() {


    }

    public StringProperty userNameProperty() {
        return wrapper.field("userName", SysUser::getUserName, SysUser::setUserName, "");
    }
}
