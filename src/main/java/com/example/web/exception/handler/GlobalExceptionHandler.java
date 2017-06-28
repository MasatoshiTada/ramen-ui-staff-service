package com.example.web.exception.handler;

import com.example.common.exception.GoodsCacheContradictionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler
    public String handleGoodsCacheContradiction(GoodsCacheContradictionException e) {
        logger.error(e.getMessage());
        return "redirect:/goods/reload";
    }
}
