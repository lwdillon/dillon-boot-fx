package com.lw.fx.client.request.feign.interceptor;

import com.lw.fx.client.store.AppStore;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class ForwardedForInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Authorization", AppStore.getToken());
    }
}