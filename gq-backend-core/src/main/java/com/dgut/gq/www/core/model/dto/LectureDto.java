package com.dgut.gq.www.core.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 接受前端传来的讲座信息
 * @version  1.0
 * @since 2022-10-25
 * @author  hyj
 */
@Data
@ApiModel(description = "新增或者修改讲座传递的json")
@AllArgsConstructor
@NoArgsConstructor
public class LectureDto {

    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(
            value = "唯一标识",
            name = "id",
            required = true
    )
    private String id;

    @ApiModelProperty(
            value = "微信公众号地址"
    )
    private  String officialAccountUrl ;


    @ApiModelProperty(value = "第几期",
            name = "term",
            required = true
    )
    private Integer term;


    @ApiModelProperty(value = "讲座名称",
            name = "lectureName",
            required = true
    )
    private String lectureName;





    @ApiModelProperty(value = "讲座预告图片地址",
            name = "image",
            required = true
    )
    private  String  image;




    @ApiModelProperty(value = "嘉宾名称",
            name = "guestName",
            required = true
    )
    private  String guestName;




    @ApiModelProperty(value = "讲座地点",
            name = "place",
            required = true
    )
    private  String place;



    @ApiModelProperty(value = "票总数",
            name = "ticketNumber",
            required = true
    )
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
    @ApiModelProperty(value = "抢票开始时间",
            name = "grabTicketsStart",
            required = true
    )
    private  LocalDateTime  grabTicketsStart;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "抢票结束时间",
            name = "grabTicketsEnd",
            required = true
    )
    private  LocalDateTime grabTicketsEnd;




    @ApiModelProperty(value = "讲座导语",
            name = "introduction",
            required = true
    )
    private  String introduction;


    @ApiModelProperty(value = "讲座回顾主题",
            required = false
    )
    private  String reviewName ;

    @ApiModelProperty(value = "讲座回顾图片路径",
            required = false
    )
    private  String reviewUrl ;


    @ApiModelProperty(value = "讲座回顾微信公众号地址",
            required = false
    )
    private  String reviewOfficialAccountUrl ;

}
