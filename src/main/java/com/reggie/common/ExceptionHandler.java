package com.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

//@ControllerAdvice(annotations = RestController.class)
// @RequestBody
@RestControllerAdvice
@Slf4j
public class ExceptionHandler {
    /**
     * 捕获重复异常
     * @param ex
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.info(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")){//表示重复
            String[] s = ex.getMessage().split(" ");
            return Result.Error(s[2]+"已存在");
        }
        return Result.Error("未知错误");
    }

    /**
     * 捕获自定义异常
     * @param ex
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
    public Result customExceptionHandler(CustomException ex){
        log.info(ex.getMessage());
        return Result.Error(ex.getMessage());
    }
}
