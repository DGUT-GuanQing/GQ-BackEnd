package com.dgut.gq.www.admin.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 讲座信息
 * @since 2022-9-16
 * @version 1.0
 * @author  hyj
 */
@Data
@TableName("gq_lecture_info")
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {

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
    private  Integer version;



    /**
     * 第几期
     */
    private Integer term;



    /**
     * 讲座名称
     */
    private String lectureName;

    /**
     * 讲座的微信号公众号地址
     */
    private  String officialAccountUrl ;


    /**
     * 讲座图片
     */
    private  String  image;




    /**
     * 嘉宾名称
     */
    private  String guestName;

    /**
     * 讲座地点
     */
    private  String place;


    /**
     * 讲座开始时间
     */
    private LocalDateTime startTime;

    /**
     * 讲座结束时间
     */
    private LocalDateTime endTime;


    /**
     * 抢票开始时间
     */
    private  LocalDateTime  grabTicketsStart;


    /**
     * 抢票结束时间
     */
    private  LocalDateTime grabTicketsEnd;

    /**
     * 票数量
     */
    private  Integer ticketNumber;

    /**
     * 讲座导语
     */
    private  String introduction;


    /**
     * 讲座回顾主题
     */
    private  String reviewName ;

    /**
     * 讲座回顾路径
     */
    private  String reviewUrl ;


    /**
     * 讲座回顾地址
     */
    private  String reviewOfficialAccountUrl ;
}
