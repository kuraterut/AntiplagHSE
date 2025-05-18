package org.kuraterut.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "word-cloud-api", url = "https://api.wordcloudapi.com")
public interface WordCloudClient {
    @PostMapping("/generate")
    String generateCloud(@RequestBody String text);
}