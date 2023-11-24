package com.dgut.gq.www.core.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "CurriculumVitaeVo",description = "返回的简历信息")
public class CurriculumVitaeVo {

    @ApiModelProperty(value = "姓名")
    private  String name;

    @ApiModelProperty(value = "学院")
    private  String college;

    @ApiModelProperty(value =  "学号")
    private  String studentId;


    @ApiModelProperty(value = "班级")
    private  String naturalClass;

    @ApiModelProperty(
            value = "部门名",
            name = "departmentName",
            required = true
    )
    private String departmentName ;


    @ApiModelProperty(
            value = "职位名",
            name = "positionName",
            required = true
    )
    private  String positionName;

    //是否调剂 0-否 1-是
    @ApiModelProperty(
            value = "调剂 0-否 1-是",
            name = "isAdjust",
            required = true
    )
    private Integer isAdjust ;


    //校区
    @ApiModelProperty(
            value = "校区 0-松山湖  1-莞城",
            name = "campus",
            required = true
    )
    private Integer campus ;

    //电话号码
    @ApiModelProperty(
            value = "手机",
            name = "phone",
            required = true
    )
    private  String  phone ;

    //微信号
    @ApiModelProperty(
            value = "微信号",
            name = "wechat",
            required = true
    )
    private  String wechat ;


    //简历的路径
    @ApiModelProperty(
            value = "简历地址",
            name = "fileUrl",
            required = true
    )
    private String fileUrl ;



    //第几期新人
    @ApiModelProperty(
            value = "第几期",
            name = "term",
            required = true
    )
    private  Integer term;

}
