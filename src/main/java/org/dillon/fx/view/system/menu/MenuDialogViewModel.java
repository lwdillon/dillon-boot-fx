package org.dillon.fx.view.system.menu;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ruoyi.common.core.web.domain.AjaxResult;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.notifications.WeakNotificationObserver;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.dillon.fx.request.Request;
import org.dillon.fx.request.feign.client.SysMenuFeign;
import org.dillon.fx.vo.SysMenu;

import java.util.HashMap;
import java.util.List;

/**
 * 菜单对话框视图模型
 *
 * @author wenli
 * @date 2023/02/12
 */
public class MenuDialogViewModel implements ViewModel, SceneLifecycle {
    public final static String ON_CLOSE = "close";
    private ObservableList<SysMenu> allMenuData = FXCollections.observableArrayList();

    /**
     * 包装器
     */
    private ModelWrapper<SysMenu> wrapper = new ModelWrapper<>();

    private Command addCommand;
    private Command edtCommand;
    private Command menuListCommand;

    private ObjectProperty<SysMenu> selectSysMenu = new SimpleObjectProperty<>(new SysMenu());


    public void initialize() {

        addCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                add();
            }
        }, true); //Async
        edtCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                edit();
            }
        }, true); //Async
        menuListCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                menuList();
            }
        }, true); //Async
    }


    /**
     * 系统设置菜单
     *
     * @param sysMenu 系统菜单
     */
    public void setSysMenu(SysMenu sysMenu) {
        wrapper.set(sysMenu);
        wrapper.reload();
    }

    /**
     * 父id属性
     *
     * @return {@link LongProperty}
     */
    public LongProperty parentIdProperty() {
        return wrapper.field("parentId", SysMenu::getParentId, SysMenu::setParentId, 0L);
    }


    /**
     * 菜单类型属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty menuTypeProperty() {
        return wrapper.field("menuType", SysMenu::getMenuType, SysMenu::setMenuType, "M");
    }

    /**
     * 图标属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty iconProperty() {
        return wrapper.field("icon", SysMenu::getIcon, SysMenu::setIcon, "");
    }

    /**
     * 菜单名称属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty menuNameProperty() {
        return wrapper.field("menuName", SysMenu::getMenuName, SysMenu::setMenuName, "");
    }

    /**
     * 显示顺序属性
     *
     * @return {@link IntegerProperty}
     */
    public IntegerProperty orderNumProperty() {
        return wrapper.field("orderNum", SysMenu::getOrderNum, SysMenu::setOrderNum, 0);
    }


    /**
     * 外链属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty isFrameProperty() {
        return wrapper.field("isFrame", SysMenu::getIsFrame, SysMenu::setIsFrame, "1");
    }

    /**
     * 路由地址属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty pathProperty() {
        return wrapper.field("path", SysMenu::getPath, SysMenu::setPath, "");
    }

    /**
     * 组件路径属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty componentProperty() {
        return wrapper.field("component", SysMenu::getComponent, SysMenu::setComponent, "");
    }

    /**
     * 权限字符串
     *
     * @return {@link StringProperty}
     */
    public StringProperty permsProperty() {
        return wrapper.field("perms", SysMenu::getPerms, SysMenu::setPerms, "");
    }

    /**
     * 路由参数属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty queryProperty() {
        return wrapper.field("query", SysMenu::getQuery, SysMenu::setQuery, "");
    }

    /**
     * 缓存属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty isCacheProperty() {
        return wrapper.field("isCache", SysMenu::getIsCache, SysMenu::setIsCache, "1");
    }

    /**
     * 可见属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty visibleProperty() {
        return wrapper.field("visible", SysMenu::getVisible, SysMenu::setVisible, "0");
    }

    /**
     * 状态属性
     *
     * @return {@link StringProperty}
     */
    public StringProperty statusProperty() {
        return wrapper.field("status", SysMenu::getStatus, SysMenu::setStatus, "0");
    }

    public SysMenu getSelectSysMenu() {
        return selectSysMenu.get();
    }

    public ObjectProperty<SysMenu> selectSysMenuProperty() {
        return selectSysMenu;
    }

    public void setSelectSysMenu(SysMenu selectSysMenu) {
        this.selectSysMenu.set(selectSysMenu);
    }

    @Override
    public void onViewAdded() {

    }

    @Override
    public void onViewRemoved() {

    }

    public Command getMenuListCommand() {
        return menuListCommand;
    }

    public ObservableList<SysMenu> getAllMenuData() {
        return allMenuData;
    }

    public Command getAddCommand() {
        return addCommand;
    }

    public Command getEdtCommand() {
        return edtCommand;
    }

    private void add() {
        wrapper.commit();
        Request.connector(SysMenuFeign.class).add(wrapper.get());
        publish(ON_CLOSE);

    }

    private void edit() {
        wrapper.commit();
        Request.connector(SysMenuFeign.class).edit(wrapper.get());
        publish(ON_CLOSE);
    }

    private void menuList() {
        JsonObject routers = Request.connector(SysMenuFeign.class).list(new HashMap<>());

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);

        JSONArray objects = JSONUtil.parseArray(array.toString());
        List<SysMenu> menus = JSONUtil.toList(objects, SysMenu.class);
        allMenuData.addAll(menus);
        publish("menuList");
    }
}
