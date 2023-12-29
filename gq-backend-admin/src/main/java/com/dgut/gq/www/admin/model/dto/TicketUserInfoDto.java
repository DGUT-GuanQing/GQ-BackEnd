package com.dgut.gq.www.admin.model.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 票号学号绑定接受类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "票号学号绑定接收类")
public class TicketUserInfoDto {

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
