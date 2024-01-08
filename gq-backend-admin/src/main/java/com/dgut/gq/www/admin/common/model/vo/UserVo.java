package com.dgut.gq.www.admin.common.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 返回给前端的用户信息
 * @author  hyj
 * @since 2022-10-10
 * @version  1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "返回用户信息")
public class UserVo {



    @ApiModelProperty(value = "姓名")
    private  String name;

    @ApiModelProperty(value = "学院")
    private  String college;

    @ApiModelProperty(value =  "学号")
    private  String studentId;


    @ApiModelProperty(value = "班级")
    private  String naturalClass;

    @ApiModelProperty(value = "是否为vip")
    private  Integer  vip;

    @ApiModelProperty(value = "参加讲座次数")
    private  Integer raceNumber;


}
