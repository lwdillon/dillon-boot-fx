package org.dillon.fx.request.feign.client;

import com.google.gson.JsonObject;
import feign.Param;
import feign.RequestLine;
import org.dillon.fx.domain.R;
import org.dillon.fx.request.feign.FeignAPI;

import java.util.Map;

public interface LoginFeign extends FeignAPI {

//    @RequestLine("GET /code")
    @RequestLine("GET /captchaImage")
    JsonObject getCode();

//    @RequestLine("POST /auth/login")
    @RequestLine("POST /login")
    JsonObject login(@Param("username") String userName, @Param("password") String password, @Param("code") String code, @Param("uuid") String uuid);

    @RequestLine("DELETE /auth/logout")
    R<?> logout();




}
