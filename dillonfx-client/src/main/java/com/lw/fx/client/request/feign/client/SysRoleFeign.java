package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.SysRole;
import com.lw.fx.client.domain.SysUserRole;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface SysRoleFeign extends FeignAPI {


    @RequestLine("GET /system/role/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);

    @RequestLine("POST /system/role/export")
    void export(SysRole role);

    /**
     * 根据角色编号获取详细信息
     */
    @RequestLine("GET /system/role/{roleId}")
    JsonObject getInfo(@Param("roleId") Long roleId);

    /**
     * 新增角色
     */
    @RequestLine("POST /system/role")
    JsonObject add(SysRole role);

    /**
     * 修改保存角色
     */
    @RequestLine("PUT /system/role")
    JsonObject edit(SysRole role);

    /**
     * 修改保存数据权限
     */
    @RequestLine("PUT /system/role/dataScope")
    JsonObject dataScope(SysRole role);

    /**
     * 状态修改
     */
    @RequestLine("PUT /system/role/changeStatus")
    JsonObject changeStatus(SysRole role);

    /**
     * 删除角色
     */
    @RequestLine("DELETE /system/role/{roleIds}")
    JsonObject remove(@Param("roleIds") String roleIds);

    /**
     * 获取角色选择框列表
     */
    @RequestLine("GET /system/role/optionselect")
    JsonObject optionselect();

    /**
     * 查询已分配用户角色列表
     */
    @RequestLine("GET /system/role/authUser/allocatedList")
    TableDataInfo allocatedList(@QueryMap Map<String, Object> query);

    /**
     * 查询未分配用户角色列表
     */
    @RequestLine("GET /system/role/authUser/unallocatedList")
    TableDataInfo unallocatedList(@QueryMap Map<String, Object> query);

    /**
     * 取消授权用户
     */
    @RequestLine("PUT /system/role/authUser/cancel")
    JsonObject cancelAuthUser(SysUserRole userRole);

    /**
     * 批量取消授权用户
     */
    @RequestLine("PUT /system/role/authUser/cancelAll")
    JsonObject cancelAuthUserAll(@QueryMap Map<String, Object> query);

    /**
     * 批量选择用户授权
     */
    @RequestLine("PUT /system/role/authUser/selectAll")
    JsonObject selectAuthUserAll(@QueryMap Map<String, Object> query);

    /**
     * 获取对应角色部门树列表
     */
    @RequestLine("GET /system/role/deptTree/{roleId}")
    JsonObject deptTree(@Param("roleId") Long roleId);
}
