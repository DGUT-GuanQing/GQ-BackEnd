package com.dgut.gq.www.core.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 讲座预告
 * @since 2023-5-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "讲座预告")
public class LectureTrailerVo {

    @TableId
    @ApiModelProperty(value = "讲座id")
    private String id;

    @ApiModelProperty(value = "微信公众号地址")
    private  String officialAccountUrl ;



    @ApiModelProperty(value = "第几期讲座")
    private  Integer term;


    @ApiModelProperty(value = "讲座名称")
    private String lectureName;


    @ApiModelProperty(value = "讲座图片地址")
    private  String  image;



    @ApiModelProperty(value = "嘉宾名称")
    private  String guestName;

}
