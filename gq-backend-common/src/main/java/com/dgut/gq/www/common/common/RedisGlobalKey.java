package com.dgut.gq.www.common.common;

/**
 * redis全局key
 * @author  hyj
 * @since  2022-10-13
 * @version  1.0
 */
public class RedisGlobalKey {

    /**
     * 权限
     */
    public  static String PERMISSION  = "gq:user:permission:";

    /**
     * 中央认证
     */
    public  static  String DGUT_LOGIN =  "gq:user:dgut_login:";

    /**
     * 用户参与的讲座
     */
    public  static  String USER_LECTURE = "gq:lecture:user_lecture:";

    /**
     * 未开始的讲座
     */
    public  static  String UNSTART_LECTURE = "gq:lecture:unstart:";


    /**
     * 锁
     */
    public  static  String LOCK_USER = "gq:user:lock:";


    /**
     * 票的数量
     */
    public static  String TICKET_NUMBER = "gq:lecture:ticket_number:";

    /**
     * 用户的信息
     */
    public  static  String USER_MESSAGE = "gq:user:user_message:";

    /**
     * 海报类型
     */
    public  static  String POSTER_TWEET =   "gq:common:poster_tweet:";


    /**
     * 是否抢到票
     */
    public  static  String IS_GRAB_TICKETS  = "gq:user:is_grab_tickets:";

}
