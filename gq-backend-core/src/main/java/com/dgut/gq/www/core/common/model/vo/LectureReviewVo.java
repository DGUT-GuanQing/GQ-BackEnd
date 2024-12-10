package com.dgut.gq.www.core.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 讲座回顾
 *
 * @since 2023-5-24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "讲座回顾")
public class LectureReviewVo {
    /**
     * 主键
     */
    @TableId
    @ApiModelProperty(value = "讲座id")
    private String id;


    @ApiModelProperty(value = "第几期讲座")
    private Integer term;


    @ApiModelProperty(value = "嘉宾名称")
    private String guestName;


    @ApiModelProperty(value = "讲座回顾主题")
    private String reviewName;


    @ApiModelProperty(value = "讲座回顾图片路径")
    private String reviewUrl;


    @ApiModelProperty(value = "讲座回顾微信公众号地址")
    private String reviewOfficialAccountUrl;
}
