package com.dgut.gq.www.common.util;


import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.guangqing.gq_common.excetion.GlobalSystemException;
import io.jsonwebtoken.Claims;

/**
 * 解析controller的额工具类
 * @author  hyj
 * @since  2022-10-11
 * @version  1.0
 */
public class ParseToken {
    /**
     * 解析传过来的token
     * @param token
     * @return
     */
    public  static String getOpenid(String token){
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            throw new GlobalSystemException(
                    GlobalResponseCode.USER_NOT_LOGIN.getCode(),
                    GlobalResponseCode.USER_NOT_LOGIN.getMessage());
        }
        String openid = claims.getSubject();
        return openid;
    }
}
