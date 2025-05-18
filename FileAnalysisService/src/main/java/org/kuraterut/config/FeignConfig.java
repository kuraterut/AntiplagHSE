package org.kuraterut.config;


import feign.Retryer;
import feign.codec.ErrorDecoder;
import org.kuraterut.feign.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(1000, 2000, 3);
    }
    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}