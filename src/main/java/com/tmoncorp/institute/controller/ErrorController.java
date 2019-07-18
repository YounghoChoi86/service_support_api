package com.tmoncorp.institute.controller;

import com.tmoncorp.common.error.domain.ErrorInfo;
import com.tmoncorp.institute.exception.InstituteDuplicationException;
import com.tmoncorp.institute.exception.InstituteNotFoundException;
import com.tmoncorp.institute.exception.InvalidArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class ErrorController {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(InstituteNotFoundException.class)
    @ResponseBody
    public ErrorInfo handleInstituteNotFoundException(HttpServletRequest req, InstituteNotFoundException e) {
        return new ErrorInfo(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InstituteDuplicationException.class)
    @ResponseBody
    public ErrorInfo handleInstituteNotFoundException(HttpServletRequest req, InstituteDuplicationException e) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseBody
    public ErrorInfo handleInvalidArgumentException(HttpServletRequest req, InvalidArgumentException e) {
        return new ErrorInfo(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }
}
