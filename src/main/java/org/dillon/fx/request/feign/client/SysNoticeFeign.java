package org.dillon.fx.request.feign.client;


import com.google.gson.JsonObject;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import org.dillon.fx.domain.SysNotice;
import org.dillon.fx.domain.page.TableDataInfo;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

/**
 * 公告 信息操作处理
 *
 * @author ruoyi
 */
public interface SysNoticeFeign extends FeignAPI {
    /**
     * 获取通知公告列表
     */
    @RequestLine("GET /system/notice/list")
    TableDataInfo list(@QueryMap Map<String, Object> map);

    /**
     * 根据通知公告编号获取详细信息
     */
    @RequestLine("GET /system/notice/{noticeId}")
    JsonObject getInfo(@Param("noticeId") Long noticeId);

    /**
     * 新增通知公告
     */
    @RequestLine("POST /system/notice")
    JsonObject add(SysNotice notice);

    /**
     * 修改通知公告
     */
    @RequestLine("PUT /system/notice")
    JsonObject edit(SysNotice notice);

    /**
     * 删除通知公告
     */
    @RequestLine("DELETE /system/notice/{noticeIds}")
    JsonObject remove(@Param("noticeIds") String noticeIds);
}
