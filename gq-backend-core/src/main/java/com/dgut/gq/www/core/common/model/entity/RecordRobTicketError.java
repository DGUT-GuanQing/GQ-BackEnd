package com.dgut.gq.www.core.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("gq_record_rob_ticket_error_info")
public class RecordRobTicketError {
    /**
     * 主键
     */
    @TableId
    private String id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime deleteTime;

    private Integer isDeleted;

    @Version
    private Integer version;

    private String openid;

    private String lectureId;

    /**
     * 0-发送交换机失败
     * 1-交换机路由到队列失败
     * 2-消费消息失败
     */
    private Integer type;

}
