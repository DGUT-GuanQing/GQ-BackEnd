package com.dgut.gq.www.core.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 简历实体类
 * @author hyj
 */
@Data
@ToString
@TableName("gq_curriculum_vitae_info")
public class CurriculumVitae {
    /**
     * 主键
     */
    @TableId
    private String id ;


    /**
     * 微信的openid
     */
    private String openid;


    private LocalDateTime createTime;


    private  LocalDateTime updateTime;


    private  LocalDateTime deleteTime ;


    private Integer isDeleted;


    //部门id，比如技术组
    private String departmentId ;



    //职位id
    private  String positionId;

    //是否调剂 0-否 1-是
    private Integer isAdjust ;


    //校区
    private Integer campus ;

    //电话号码
    private  String  phone ;

    //微信号
    private  String wechat ;


    //简历的路径
    private String fileUrl ;


    //第几期新人
    private  Integer term;
}
