package com.dgut.gq.www.recruit.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 接受前端传来的用户信息
 * @version  1.0
 * @since 2022-10-25
 * @author  hyj
 */
@Data
@ApiModel(description = "接受用户信息的实现类")
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @ApiModelProperty(value = "用户姓名",
            name = "name",
            allowEmptyValue = false,
            required = true
    )
    private  String name;

    @ApiModelProperty(value = "学院",
            name = "department",
            allowEmptyValue = false,
            required = true
    )
    private  String college;

    @ApiModelProperty(value = "学号",
            name = "studentId",
            allowEmptyValue = false,
            required = true
    )
    private  String studentId;

    @ApiModelProperty(value = "班级",
            name = "naturalClass",
            allowEmptyValue =false,
            required = true
    )
    private  String naturalClass;

}
