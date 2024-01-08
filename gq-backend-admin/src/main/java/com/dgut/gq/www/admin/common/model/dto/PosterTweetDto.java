package com.dgut.gq.www.admin.common.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @since  2023-3-14
 * @author  hyj
 */
@Data
@ApiModel(description = "接受招新或者活动推文信息的实现类")
@NoArgsConstructor
@AllArgsConstructor
public class PosterTweetDto {
    /**
     * 主键
     */
    @TableId
    private String id;


    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "图片地址")
    private  String image;

    @ApiModelProperty(value = "微信公众号地址")
    private  String officialAccountUrl;

    @ApiModelProperty(value =  "类型 0-活动 1-招新")
    private Integer type;
}
