package com.dgut.gq.www.core.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 票号学号绑定实体类
 */
@Data
@TableName("gq_ticket_user_info")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "票号学号绑定")
public class TicketUserInfo {

    @TableId
    String  id ;

    /**
     * 票号
     */
    String ticketId ;


    /**
     * 学号
     */
    String studentId ;


    private Integer isDeleted;


    private LocalDateTime createTime;




    private  LocalDateTime updateTime;


}
