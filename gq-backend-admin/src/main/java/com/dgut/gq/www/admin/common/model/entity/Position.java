package com.dgut.gq.www.admin.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 职位实体类
 */
@Data
@TableName("gq_position_info")
@NoArgsConstructor
@AllArgsConstructor
public class Position {
    @TableId
    private String id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime deleteTime;

    private Integer isDeleted;

    private int version;

    /**
     * 职位名
     */
    private String positionName;

    /**
     * 部门id
     */
    private  String departmentId;
}
