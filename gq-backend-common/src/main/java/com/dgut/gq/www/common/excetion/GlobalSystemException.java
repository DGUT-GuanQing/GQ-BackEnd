package com.dgut.gq.www.common.excetion;


import com.dgut.gq.www.common.common.GlobalResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 定义全局异常类
 *
 * @author hyj
 * @version 1.0
 * @since 2022-10-11
 */
@Data
@NoArgsConstructor
public class GlobalSystemException extends RuntimeException {

    public int code;

    public String msg;

    public GlobalSystemException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public GlobalSystemException(GlobalResponseCode globalResponseCode) {
        this.code = globalResponseCode.getCode();
        this.msg = globalResponseCode.getMessage();
    }

}
