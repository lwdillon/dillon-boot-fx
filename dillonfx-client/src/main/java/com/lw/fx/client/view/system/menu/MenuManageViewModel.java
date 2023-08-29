package com.lw.fx.client.view.system.menu;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lw.fx.client.domain.AjaxResult;
import com.lw.fx.client.domain.SysMenu;
import com.lw.fx.client.request.Request;
import com.lw.fx.client.request.feign.client.SysMenuFeign;
import de.saxsys.mvvmfx.SceneLifecycle;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 菜单管理视图模型
 *
 * @author wenli
 * @date 2023/02/15
 */
public class MenuManageViewModel implements ViewModel, SceneLifecycle {
    public final static String OPEN_ALERT = "OPEN_ALERT";

    private SimpleStringProperty searchText = new SimpleStringProperty("");
    private SimpleStringProperty statusText = new SimpleStringProperty("全部");


    private ObservableList<SysMenu> allData = FXCollections.observableArrayList();

    private List<SysMenu> menuList = CollUtil.newArrayList();

    public String getSearchText() {
        return searchText.get();
    }

    public SimpleStringProperty searchTextProperty() {
        return searchText;
    }

    public String getStatusText() {
        return statusText.get();
    }

    public SimpleStringProperty statusTextProperty() {
        return statusText;
    }

    public void initialize() {

    }

    public void someAction() {
        publish(OPEN_ALERT, "Some Error has happend");
    }


    @Override
    public void onViewAdded() {
        System.err.println("------add");
    }


    public ObservableList<SysMenu> getAllData() {
        return allData;
    }


    public List<SysMenu> getMenuList() {
        return menuList;
    }

    @Override
    public void onViewRemoved() {
        unsubscribe(OPEN_ALERT, (s, objects) -> {
        });
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toList());
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext(); ) {
            SysMenu menu = (SysMenu) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return getChildList(list, t).size() > 0;
    }


    /**
     * 获取菜单列表
     */

    public void query() {
        menuList.clear();
        String status = statusText.getValue();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("menuName", searchText.getValue());
        queryMap.put("status", StrUtil.equals("全部", status) ? "" : (StrUtil.equals("正常", status) ? "0" : "1"));
        JsonObject routers = Request.connector(SysMenuFeign.class).list(queryMap);

        JsonArray array = routers.getAsJsonArray(AjaxResult.DATA_TAG);
        List<SysMenu> sysMenuList = buildMenuTree(JSONUtil.toList(array.toString(), SysMenu.class));

        menuList.addAll(sysMenuList);
    }

    public void remove(Long menuId) {
        Request.connector(SysMenuFeign.class).remove(menuId);

    }

    public void rest() {
        searchTextProperty().setValue("");
        statusText.setValue("全部");
    }


}
