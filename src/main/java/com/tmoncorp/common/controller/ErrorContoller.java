package com.tmoncorp.common.controller;

import com.tmoncorp.common.error.domain.ErrorInfo;
import com.tmoncorp.common.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ErrorContoller {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ErrorInfo handleException(HttpServletRequest req, ValidationException e) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
