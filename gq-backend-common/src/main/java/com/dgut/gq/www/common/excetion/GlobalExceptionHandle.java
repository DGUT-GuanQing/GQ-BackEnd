package com.dgut.gq.www.common.excetion;

import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.common.SystemJsonResponse;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-16
 */
//@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandle {

    /**
     * 自定义异常处理
     *
     * @return
     */
    @ExceptionHandler(GlobalSystemException.class)
    public SystemJsonResponse systemJsonResponse(GlobalSystemException e) {
        return SystemJsonResponse.fail(e.getCode(), e.getMsg());
    }

    @Order(2)
    @ExceptionHandler(Exception.class)
    public SystemJsonResponse commonException(Exception e) {
        return SystemJsonResponse.fail(GlobalResponseCode.SYSTEM_TIMEOUT.getCode(), e.getMessage());
    }

}
