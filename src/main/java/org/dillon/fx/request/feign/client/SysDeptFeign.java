package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysDept;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

/**
 * 部门信息
 *
 * @author ruoyi
 */

public interface SysDeptFeign extends FeignAPI {

    /**
     * 获取部门列表
     */
    @RequestLine("GET /system/dept/list")
    JsonObject list(@QueryMap Map<String,Object> map);

    /**
     * 查询部门列表（排除节点）
     */
    @RequestLine("GET /system/dept/list/exclude/{deptId}")
    JsonObject excludeChild(@Param("deptId") Long deptId);

    /**
     * 根据部门编号获取详细信息
     */
    @RequestLine("GET /system/dept/{deptId}")
    JsonObject getInfo(@Param("deptId") Long deptId);

    /**
     * 新增部门
     */
    @RequestLine("POST /system/dept")
    JsonObject add(SysDept dept);

    /**
     * 修改部门
     */
    @RequestLine("PUT /system/dept")
    JsonObject edit(SysDept dept);

    /**
     * 删除部门
     */
    @RequestLine("DELETE /system/dept/{deptId}")
    JsonObject remove(@Param("deptId") Long deptId);
}
