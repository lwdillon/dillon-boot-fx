package com.lw.fx.client.request.feign.client;

import com.google.gson.JsonObject;
import com.lw.fx.client.domain.R;
import com.lw.fx.client.request.feign.FeignAPI;
import feign.Param;
import feign.RequestLine;

public interface LoginFeign extends FeignAPI {

    //        @RequestLine("GET /code")
    @RequestLine("GET /captchaImage")
    JsonObject getCode();

    //        @RequestLine("POST /auth/login")
    @RequestLine("POST /login")
    JsonObject login(@Param("username") String userName, @Param("password") String password, @Param("code") String code, @Param("uuid") String uuid);

    @RequestLine("DELETE /auth/logout")
    R<?> logout();


}
