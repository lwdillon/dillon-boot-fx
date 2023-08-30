package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.LoginUser;
import com.lw.fx.client.domain.R;
import com.lw.fx.client.domain.SysUser;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

public interface SysUserFeign extends FeignAPI {

    //    @RequestLine("GET /system/user/getInfo")
    @RequestLine("GET /getInfo")
    Map<String, Object> getInfo();


    /**
     * 获取用户列表
     */
    @RequestLine("GET /system/user/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);


    /**
     * 获取当前用户信息
     */
    @RequestLine("GET /system/user/info/{username}")
    R<LoginUser> info(@Param("username") String username);

    /**
     * 注册用户信息
     */
    @RequestLine("POST /system/user/register")
    R<Boolean> register(SysUser sysUser);


    /**
     * 根据用户编号获取详细信息
     */
    @RequestLine("GET /system/user/{userId}")
    JsonObject getInfo(@Param("userId") Long userId);

    /**
     * 新增用户
     */
    @RequestLine("POST /system/user")
    JsonObject add(SysUser user);

    /**
     * 修改用户
     */
    @RequestLine("PUT /system/user")
    JsonObject edit(SysUser user);

    /**
     * 删除用户
     */
    @RequestLine("DELETE /system/user/{userIds}")
    JsonObject remove(@Param("userIds") String userIds);

    /**
     * 重置密码
     */
    @RequestLine("PUT /system/user/resetPwd")
    JsonObject resetPwd(SysUser user);

    /**
     * 状态修改
     */
    @RequestLine("PUT /system/user/changeStatus")
    JsonObject changeStatus(SysUser user);

    /**
     * 根据用户编号获取授权角色
     */
    @RequestLine("GET /system/user/authRole/{userId}")
    JsonObject authRole(@Param("userId") Long userId);

    /**
     * 用户授权角色
     */
    @RequestLine("PUT /system/user/authRole")
    JsonObject insertAuthRole(@QueryMap Map<String, Object> query);

    /**
     * 获取部门树列表
     */
    @RequestLine("GET /system/user/deptTree")
    JsonObject deptTree(@QueryMap Map<String, Object> query);


}
