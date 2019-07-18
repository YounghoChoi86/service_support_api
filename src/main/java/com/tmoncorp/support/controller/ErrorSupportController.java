package com.tmoncorp.support.controller;

import com.tmoncorp.common.error.domain.ErrorInfo;
import com.tmoncorp.support.exception.SupportNotFouncException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ErrorSupportController {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SupportNotFouncException.class)
    @ResponseBody
    public ErrorInfo handleInstituteNotFoundException(HttpServletRequest req, SupportNotFouncException e) {
        return new ErrorInfo(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }
}
