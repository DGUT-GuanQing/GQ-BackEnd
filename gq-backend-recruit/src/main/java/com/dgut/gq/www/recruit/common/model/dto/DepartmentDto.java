package com.dgut.gq.www.recruit.common.model.dto;



import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hyj
 * @since 2023-5-30
 */

@Data
@ApiModel(description = "接受部门的类")
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
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
