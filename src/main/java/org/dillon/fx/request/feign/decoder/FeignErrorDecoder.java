package org.dillon.fx.request.feign.decoder;

import feign.Response;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;

import java.io.IOException;

public class FeignErrorDecoder implements ErrorDecoder {

    final Decoder decoder;
    final ErrorDecoder defaultDecoder = new Default();

    public FeignErrorDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            return (Exception) decoder.decode(response, FeignError.class);
        } catch (IOException fallbackToDefault) {
            return defaultDecoder.decode(methodKey, response);
        }
    }
}