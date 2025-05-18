package org.kuraterut;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "org.kuraterut.feign") // Указываем пакет с Feign-клиентами
public class FileAnalysisApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileAnalysisApplication.class, args);
    }
}