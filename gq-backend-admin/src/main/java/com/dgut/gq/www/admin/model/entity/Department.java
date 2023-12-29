package com.dgut.gq.www.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * @author  hyj
 * 部门实体类
 */
@Data
@ToString
@TableName("gq_department_info")
@NoArgsConstructor
@AllArgsConstructor
public class Department {

        @TableId
        private String id;

        private LocalDateTime createTime;


        private LocalDateTime updateTime;


        private LocalDateTime deleteTime;


        private Integer isDeleted;


        private int version;

       /**
       * 部门名
       */
       private String departmentName;


}
