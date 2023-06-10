package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysOperLog;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

/**
 * 操作日志记录
 *
 * @author ruoyi
 */
public interface SysOperlogFeign extends FeignAPI {
//    @RequestLine("GET /system/operlog/list")
    @RequestLine("GET /monitor/operlog/list")
    TableDataInfo list(@QueryMap Map<String,Object> map);

    @RequestLine("POST /system/operlog/export")
    void export(SysOperLog operLog);

    @RequestLine("DELETE /system/operlog/{operIds}")
    JsonObject remove(@Param("operIds") String operIds);

    @RequestLine("DELETE /system/operlog/clean")
    JsonObject clean();

    @RequestLine("POST /system/operlog")
    JsonObject add(SysOperLog operLog);
}
