package com.dgut.gq.www.common.excetion;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 定义全局异常类
 * @author  hyj
 * @since  2022-10-11
 * @version  1.0
 */
@Data
@NoArgsConstructor

public class GlobalSystemException extends  RuntimeException{

    public int  code;
    public  String msg;

  public  GlobalSystemException(int  code,String msg){
      this.code=code;
      this.msg=msg;
  }

}
