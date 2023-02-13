package org.dillon.fx.request.feign.client;

import feign.RequestLine;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

public interface SysUserFeign extends FeignAPI {

    @RequestLine("GET /system/user/getInfo")
    Map<String, Object> getInfo();
}
