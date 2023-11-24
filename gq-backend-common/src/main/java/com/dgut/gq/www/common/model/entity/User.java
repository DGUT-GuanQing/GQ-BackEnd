package com.dgut.gq.www.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息
 * 包括微信端和管理端
 * @author hyj
 * @since 2022-09-02
 * @version 1.0
 *
 */
@Data
@TableName("gq_user_info")
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户实体类")
public class User {

    /**
     * uuid主键
     */
    @TableId
    private String uuid ;


    /**
     * 微信的openid
     */
    private String openid;



    private LocalDateTime createTime;




    private  LocalDateTime updateTime;




    private  LocalDateTime deleteTime ;




    private Integer isDeleted;


    /**
     * 版本号乐观锁
     */
    @Version
    private  Integer version;


    /**
     * 用户名
     */
    private  String userName;


    /**
     * 密码
     */
    private  String password;





    /**
     * 名字
     */
    private  String name;


    /**
     * 性别
     */
    private  Integer sex;


    /**
     * 学院
     */
    private  String college;


    /**
     * 学号
     */
    private  String studentId;



    /**
     * 班级
     */
    private  String naturalClass;


    /**
     * 老师或者学生标号
     */
    private  Integer userIdentity;


    /**
     * 是否是vip
     */
    private  Integer  vip;


    /**
     * 参加讲座次数
     */
    private  Integer raceNumber;




    /**
     * 权限
     */
    private  String permission;
}
