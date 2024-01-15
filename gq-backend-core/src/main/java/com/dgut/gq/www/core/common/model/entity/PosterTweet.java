package com.dgut.gq.www.core.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("gq_poster_tweet_info")
public class PosterTweet {
    /**
     * 主键
     */
    @TableId
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片链接
     */
    private  String image;

    /**
     * 微信公众号地址
     */
    private  String officialAccountUrl;


    /**
     * 类型
     */
    private  Integer type;

    private LocalDateTime createTime;



    private LocalDateTime updateTime;



    private LocalDateTime deleteTime;



    private Integer isDeleted;



    @Version
    private  Integer version;
}
