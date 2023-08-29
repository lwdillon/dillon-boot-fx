package com.lw.fx.client.request.feign.client;


import com.google.gson.JsonObject;
import com.lw.fx.client.domain.SysDictType;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
public interface SysDictTypeFeign extends FeignAPI {
    @RequestLine("GET /system/dict/type/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);

    @RequestLine("GET /system/dict/type/export")
    void export( SysDictType dictType);

    /**
     * 查询字典类型详细
     */
    @RequestLine("GET /system/dict/type/{dictId}")
    JsonObject getInfo(@Param("dictId") Long dictId);

    /**
     * 新增字典类型
     */
    @RequestLine("POST /system/dict/type")
    JsonObject add(SysDictType dict);

    /**
     * 修改字典类型
     */
    @RequestLine("PUT /system/dict/type")
    JsonObject edit(SysDictType dict);

    /**
     * 删除字典类型
     */
    @RequestLine("DELETE /system/dict/type/{dictIds}")
    JsonObject remove(@Param("dictIds") String dictIds);

    /**
     * 刷新字典缓存
     */
    @RequestLine("DELETE /system/dict/type/refreshCache")
    JsonObject refreshCache();

    /**
     * 获取字典选择框列表
     */
    @RequestLine("GET /system/dict/type/optionselect")
    JsonObject optionselect();
}
