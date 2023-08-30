package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.SysOperLog;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
public interface SysOperlogFeign extends FeignAPI {
    //    @RequestLine("GET /system/operlog/list")
    @RequestLine("GET /monitor/operlog/list")
    TableDataInfo list(@QueryMap Map<String, Object> map);

    @RequestLine("POST /system/operlog/export")
    void export(SysOperLog operLog);

    @RequestLine("DELETE /system/operlog/{operIds}")
    JsonObject remove(@Param("operIds") String operIds);

    @RequestLine("DELETE /system/operlog/clean")
    JsonObject clean();

    @RequestLine("POST /system/operlog")
    JsonObject add(SysOperLog operLog);
}
