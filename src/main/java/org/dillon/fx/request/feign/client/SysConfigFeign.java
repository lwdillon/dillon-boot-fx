package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysConfig;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

/**
 * 参数配置 信息操作处理
 *
 * @author ruoyi
 */
public interface SysConfigFeign extends FeignAPI {

    /**
     * 获取参数配置列表
     */
    @RequestLine("GET /system/config/list")
    TableDataInfo list(@QueryMap Map<String, Object> map);

    @RequestLine("GET /system/config/export")
    void export(SysConfig config);

    /**
     * 根据参数编号获取详细信息
     */
    @RequestLine("GET /system/config/{configId}")
    JsonObject getInfo(@Param("configId") Long configId);

    /**
     * 根据参数键名查询参数值
     */
    @RequestLine("GET /system/config/configKey/{configKey}")
    JsonObject getConfigKey(@Param("configKey") String configKey);

    /**
     * 新增参数配置
     */
    @RequestLine("POST /system/config")
    JsonObject add(SysConfig config);

    /**
     * 修改参数配置
     */
    @RequestLine("PUT /system/config")
    JsonObject edit(SysConfig config);

    /**
     * 删除参数配置
     */
    @RequestLine("DELETE /system/config/{configIds}")
    JsonObject remove(@Param("configIds") String configIds);

    /**
     * 刷新参数缓存
     */
    @RequestLine("DELETE /system/config/refreshCache")
    JsonObject refreshCache();
}
