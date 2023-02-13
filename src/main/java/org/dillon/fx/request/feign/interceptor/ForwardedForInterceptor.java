package org.dillon.fx.request.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.dillon.fx.store.AppStore;

public class ForwardedForInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {

        template.header("Authorization", AppStore.getToken());
    }
}