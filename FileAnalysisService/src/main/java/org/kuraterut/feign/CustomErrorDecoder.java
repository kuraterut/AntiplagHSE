package org.kuraterut.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        // Обрабатываем специфичные статусы
        if (response.status() == 404) {
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "File not found in storage service"
            );
        }

        if (response.status() == 503) {
            return new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Storage service unavailable"
            );
        }

        // Для остальных ошибок используем стандартный обработчик
        return defaultDecoder.decode(methodKey, response);
    }
}