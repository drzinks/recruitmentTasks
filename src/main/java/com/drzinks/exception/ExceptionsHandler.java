package com.drzinks.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE) //it is due to Spring always handled Throwables
@ControllerAdvice()
@Slf4j
public class ExceptionsHandler {

    @ExceptionHandler(GitHubApiException.class)
    public ResponseEntity<ApiError> handleException(GitHubApiException e) {
        log.error(e.getApiError().toString());
        return new ResponseEntity<ApiError>(e.getApiError(), HttpStatus.valueOf(e.getApiError().getStatus()));

    }

}
