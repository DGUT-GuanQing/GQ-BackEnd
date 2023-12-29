package com.dgut.gq.www.admin.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(description = "返回的推文信息")
public class PosterTweetVo {
    /**
     * 主键
     */
    @TableId
    private String id;

    @ApiModelProperty(value = "标题")
    private String title;


    /**
     * 图片链接
     */
    @ApiModelProperty(value = "图片地址")
    private  String image;

    /**
     * 微信公众号地址
     */
    @ApiModelProperty(value = "微信公众号地址")
    private  String officialAccountUrl;

    @ApiModelProperty(value =  "类型")
    private Integer type;
}
