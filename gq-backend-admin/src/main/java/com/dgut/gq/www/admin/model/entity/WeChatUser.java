package com.dgut.gq.www.admin.model.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 接受前端传过来的信息
 * 学号和密码
 * @since  2022-9-16
 * @author hyj
 * @version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "后台登录实体")
public class WeChatUser {
    @ApiModelProperty(value = "用户名",
            name ="userName",
            required = true
    )
    private  String userName;


    @ApiModelProperty(value = "学号",
            name = "studentId",
            required = true
    )
    private String studentId;


    @ApiModelProperty(value = "密码",
            name = "password",
            required = true
    )
    private  String password;


    @ApiModelProperty(value = "openid 不用填",
            name = "openid",
            allowEmptyValue = true
    )
    private  String openid;
}
