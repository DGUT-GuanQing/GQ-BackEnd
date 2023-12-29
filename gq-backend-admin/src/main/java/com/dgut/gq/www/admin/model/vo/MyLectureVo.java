package com.dgut.gq.www.admin.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 我参加的讲座
 * @since 2023-5-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "我参加的讲座")
public class MyLectureVo {
    /**
     * 主键
     */
    @TableId
    private String id;

    @ApiModelProperty(value = "讲座预告微信公众号地址")
    private  String officialAccountUrl ;



    @ApiModelProperty(value = "第几期讲座")
    private  Integer term;


    @ApiModelProperty(value = "讲座预告名称")
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


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "讲座开始时间",
            name = "startTime",
            required = true
    )
    private LocalDateTime startTime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "讲座结束时间",
            required = true
    )
    private  LocalDateTime endTime;


    @ApiModelProperty(value = "0或者1-表示未观看 2-表示观看",
            required = true
    )
   private  Integer status;


}
