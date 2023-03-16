package com.reggie.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 公共字段自动更新,插入
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    HttpServletRequest request;
    /**
     * 公共字段自动插入
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
log.info("公共字段插入操作"+metaObject.toString()+request.getSession().getAttribute("employeeId")
);
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser",ThreadLocalUtil.getCurrentId());
        metaObject.setValue("updateUser", ThreadLocalUtil.getCurrentId());
    }

    /**
     * 公共字段自动更新
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("公共字段更新操作"+metaObject.toString()+"+"+request.getSession().getAttribute("employeeId"));
        log.info("线程id:"+Thread.currentThread().getId());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",ThreadLocalUtil.getCurrentId());
    }
}
