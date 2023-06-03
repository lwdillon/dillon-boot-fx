package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysDictData;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.request.feign.FeignAPI;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 数据字典信息
 *
 * @author ruoyi
 */
public interface SysDictDataFeign extends FeignAPI {
    @RequestLine("GET /system/dict/data/list")
    TableDataInfo list(@QueryMap Map<String, Object> query);

    @RequestLine("POST /system/dict/data/export")
    void export(SysDictData dictData);

    /**
     * 查询字典数据详细
     */
    @RequestLine("GET /system/dict/data/{dictCode}")
    JsonObject getInfo(@Param("dictCode") Long dictCode);

    /**
     * 根据字典类型查询字典数据信息
     */
    @RequestLine("GET /system/dict/data/type/{dictType}")
    JsonObject dictType(@Param("dictType") String dictType);

    /**
     * 新增字典类型
     */
    @RequestLine("POST /system/dict/data")
    JsonObject add(SysDictData dict);

    /**
     * 修改保存字典类型
     */
    @RequestLine("PUT /system/dict/data")
    JsonObject edit(SysDictData dict);

    /**
     * 删除字典类型
     */
    @RequestLine("DELETE /system/dict/data/{dictCodes}")
    JsonObject remove(@Param("dictCodes") String dictCodes);
}
