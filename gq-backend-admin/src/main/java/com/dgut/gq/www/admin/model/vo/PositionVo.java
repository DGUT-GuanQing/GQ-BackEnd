package com.dgut.gq.www.admin.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 职位vo
 */
@Data
@ApiModel(description = "返回给前端的职位信息")
@NoArgsConstructor
@AllArgsConstructor
public class PositionVo {
    @TableId
    @ApiModelProperty(
            value = "职位id",
            name = "id",
            required = true
    )
    private String id;


    /**
     * 职位名
     */
    @ApiModelProperty(
            value = "职位名",
            name = "positionName",
            required = true
    )
    private String positionName;


    /**
     * 部门id
     */
    @ApiModelProperty(
            value = "部门id",
            name = "departmentId",
            required = true
    )
    private  String departmentId;
}
