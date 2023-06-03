package org.dillon.fx.request.feign.client;


import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysPost;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

/**
 * 岗位信息操作处理
 *
 * @author ruoyi
 */

public interface SysPostFeign extends FeignAPI {

    /**
     * 获取岗位列表
     */
    @RequestLine("GET /system/post/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);


    /**
     * 根据岗位编号获取详细信息
     */
    @RequestLine("GET /system/post/{postId}")
    JsonObject getInfo(@Param("postId") Long postId);

    /**
     * 新增岗位
     */
    @RequestLine("POST /system/post")
    JsonObject add(SysPost post);

    /**
     * 修改岗位
     */
    @RequestLine("PUT /system/post")
    JsonObject edit(SysPost post);

    /**
     * 删除岗位
     */
    @RequestLine("DELETE /system/post/{postIds}")
    JsonObject remove(@Param("postIds") String postIds);

    /**
     * 获取岗位选择框列表
     */
    @RequestLine("GET /system/post/optionselect")
    JsonObject optionselect();
}
