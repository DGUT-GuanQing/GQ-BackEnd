package com.dgut.gq.www.admin.common.model.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "LectureVo",description = "返回讲座信息")
public class LectureVo {
    /**
     * 主键
     */
    @TableId
    private String id;

    @ApiModelProperty(value = "讲座预告微信公众号地址")
    private  String officialAccountUrl ;



    @ApiModelProperty(value = "第几期讲座")
    private  Integer term;


    @ApiModelProperty(value = "讲座名称")
    private String lectureName;


    @ApiModelProperty(value = "讲座预告图片地址")
    private  String  image;



    @ApiModelProperty(value = "嘉宾名称")
    private  String guestName;


    @ApiModelProperty(value = "讲座地点",
            name = "place",
            required = true
    )
    private  String place;


    @ApiModelProperty(value = "票数量")
    private  Integer ticketNumber;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "讲座开始时间",
            name = "startTime",
            required = true
    )
    private  LocalDateTime  startTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "讲座结束时间",
            required = true
    )
    private  LocalDateTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "抢票开始时间")
    private  LocalDateTime  grabTicketsStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "抢票结束时间")
    private  LocalDateTime grabTicketsEnd;



    @ApiModelProperty(value = "讲座导语",
            name = "introduction",
            required = true
    )
    private  String introduction;


    @ApiModelProperty(value = "讲座回顾主题")
    private  String reviewName ;



    @ApiModelProperty(value = "讲座回顾图片路径")
    private  String reviewUrl ;



    @ApiModelProperty(value = "讲座回顾微信公众号地址")
    private  String reviewOfficialAccountUrl ;


}
