package com.dgut.gq.www.recruit.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部门vo
 */
@Data
@ApiModel(description = "返回给前端的部门信息")
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentVo {
    @TableId
    @ApiModelProperty(
            value = "部门id",
            name = "id",
            required = true
    )
    private String id;

    /**
     * 部门名
     */
    @ApiModelProperty(
            value = "部门名",
            name = "id",
            required = true
    )
    private String departmentName;
}
