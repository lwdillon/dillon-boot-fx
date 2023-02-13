package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import com.ruoyi.common.core.web.domain.AjaxResult;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.request.feign.FeignAPI;
import org.dillon.fx.vo.SysMenu;

import java.util.List;
import java.util.Map;

public interface SysMenuFeign extends FeignAPI {


    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @RequestLine("GET /system/menu/getRouters")
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
}
