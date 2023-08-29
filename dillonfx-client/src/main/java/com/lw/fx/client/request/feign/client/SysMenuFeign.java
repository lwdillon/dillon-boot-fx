package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.SysMenu;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface SysMenuFeign extends FeignAPI {


    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
//    @RequestLine("GET /system/menu/getRouters")
    @RequestLine("GET /getRouters")
    JsonObject getRouters();


    /**
     * 获取菜单列表
     */
    @RequestLine("GET /system/menu/list")
    JsonObject list(@QueryMap Map<String, Object> query);

    /**
     * 获取菜单列表
     */
    @RequestLine("POST /system/menu")
    JsonObject add(SysMenu sysMenu);

    /**
     * 删除菜单
     */
    @RequestLine("DELETE /system/menu/{menuId}")
    JsonObject remove(@Param("menuId") Long menuId);

    /**
     * 修改菜单
     */
    @RequestLine("PUT /system/menu")
    JsonObject edit(SysMenu menu);

    /**
     * 获取菜单下拉树列表
     */
    @RequestLine("GET /system/menu/treeselect")
    JsonObject treeselect(@QueryMap Map<String, Object> query);


    @RequestLine("GET /system/menu/roleMenuTreeselect/{roleId}")
    JsonObject roleMenuTreeselect(@Param("roleId") Long roleId);

}
