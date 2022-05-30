package com.coursework.web.exceptions;

import com.coursework.domain.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ExceptionAdvice {

    Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception e) throws Throwable {
        logger.error("Request URL : {}，Exception : {}", request.getRequestURL(), e);

        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("uri",request.getRequestURI());
        modelAndView.addObject("exception", e.getMessage());
        modelAndView.addObject("status", INTERNAL_SERVER_ERROR.value());

        return modelAndView;
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFoundException(HttpServletRequest request, NotFoundException e) {
        logger.error("Request URL : {}，Exception : {}", request.getRequestURL(), e.getMessage());

        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("uri",request.getRequestURI());
        modelAndView.addObject("exception", e.getMessage());
        modelAndView.addObject("status", NOT_FOUND.value());

        return modelAndView;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleMaxSizeException(HttpServletRequest request, MaxUploadSizeExceededException e) {
        logger.error("Request URL : {}，Exception : {}", request.getRequestURL(), e.getMessage());

        ModelAndView modelAndView = new ModelAndView("/error");
        modelAndView.addObject("uri",request.getRequestURI());
        modelAndView.addObject("exception", e.getMessage());
        modelAndView.addObject("status", BAD_REQUEST.value());

        return modelAndView;
    }

//    @ResponseStatus(BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ErrorMessage handleIllegalArgumentException(IllegalArgumentException e) {
//        String message = e.getMessage();
//        logger.error(message, e);
//        return new ErrorMessage(message);
//    }
}

