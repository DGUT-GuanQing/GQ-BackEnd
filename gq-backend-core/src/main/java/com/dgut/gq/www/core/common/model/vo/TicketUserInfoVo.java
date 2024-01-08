package com.dgut.gq.www.core.common.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 票号学号绑返回类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "票号学号绑定返回类")
public class TicketUserInfoVo {

    @TableId
    String  id ;

    @ApiModelProperty(value = "票号",
            name = "ticketId",
            allowEmptyValue = false,
            required = true
    )
    String ticketId ;


    @ApiModelProperty(value = "学号",
            name = "studentId",
            allowEmptyValue = false,
            required = true
    )
    String studentId ;




}
