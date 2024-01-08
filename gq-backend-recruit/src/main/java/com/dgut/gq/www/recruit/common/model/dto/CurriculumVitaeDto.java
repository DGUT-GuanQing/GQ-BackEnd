package com.dgut.gq.www.recruit.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 接受前端传来简历信息
 * @author  hyj
 */
@Data
@ApiModel(description = "接受报名信息的实现类")
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumVitaeDto {

    @ApiModelProperty(value = "班级",
            name = "naturalClass",
            allowEmptyValue =false,
            required = true
    )
    private  String naturalClass;

    @TableId
    @ApiModelProperty(
            value = "部门id",
            name = "departmentId",
            required = true
    )
    private String departmentId ;

    //职位
    @ApiModelProperty(
            value = "职位id",
            name = "positionId",
            required = true
    )
    private  String positionId;

    //是否调剂 0-否 1-是
    @TableId
    @ApiModelProperty(
            value = "调剂 0-否 1-是",
            name = "isAdjust",
            required = true
    )
    private Integer isAdjust ;

    //校区
    @TableId
    @ApiModelProperty(
            value = "校区 0-松山湖  1-莞城",
            name = "campus",
            required = true
    )
    private Integer campus ;

    //电话号码
    @TableId
    @ApiModelProperty(
            value = "手机",
            name = "phone",
            required = true
    )
    private  String  phone ;

    //微信号
    @TableId
    @ApiModelProperty(
            value = "微信号",
            name = "wechat",
            required = true
    )
    private  String wechat ;

    //简历的路径
    @TableId
    @ApiModelProperty(
            value = "简历地址",
            name = "fileUrl",
            required = true
    )
    private String fileUrl ;
}
