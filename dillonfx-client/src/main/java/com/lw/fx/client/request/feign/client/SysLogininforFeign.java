package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.SysLogininfor;
import com.lw.fx.client.domain.page.TableDataInfo;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.Map;

/**
 * 系统访问记录
 *
 * @author ruoyi
 */
public interface SysLogininforFeign extends FeignAPI {
    @RequestLine("GET /monitor/logininfor/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);

    @RequestLine("POST /system/logininfor/export")
    void export(SysLogininfor logininfor);

    @RequestLine("DELETE /system/logininfor/{infoIds}")
    JsonObject remove(@Param("infoIds") String infoIds);

    @RequestLine("DELETE /system/logininfor/clean")
    JsonObject clean();

    @RequestLine("GET /system/logininfor/unlock/{userName}")
    JsonObject unlock(@Param("userName") String userName);

    @RequestLine("POST /system/logininfor")
    JsonObject add(SysLogininfor logininfor);
}
