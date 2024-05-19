package com.evergreen.evergreenserver.global.exception;

import com.evergreen.evergreenserver.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ApiResponse> apiExceptionHandler(ApiException ex) {
    return new ResponseEntity<>(new ApiResponse(ex.getMsg(), ex.getStatus().value()),
        ex.getStatus());
  }

}