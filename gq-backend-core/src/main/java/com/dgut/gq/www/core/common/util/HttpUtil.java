package com.dgut.gq.www.core.common.util;


import com.alibaba.fastjson.JSONObject;
import com.dgut.gq.www.common.common.GlobalResponseCode;
import com.dgut.gq.www.common.excetion.GlobalSystemException;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

/**
 * 获取openid的工具类
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-16
 */
@Slf4j
@Service
public class HttpUtil {

    /**
     * 通过code获取信息
     *
     * @param code
     * @return
     */
    private static String appid;

    private static String secret;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    @Value("${wx.appid}")
    public void setAppid(String appid) {
        HttpUtil.appid = appid;
    }

    @Value("${wx.secret}")
    public void setSecret(String secret) {
        HttpUtil.secret = secret;
    }

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    public String getOpenid(String code) {
        String url = WX_LOGIN_URL + "?appid=" + appid + "&secret=" + secret + "&js_code=" + code + "&grant_type=authorization_code";
        JSONObject jsonObject = restTemplate.getForObject(url, JSONObject.class);
        Optional<JSONObject> optionalJSONObject = Optional.ofNullable(jsonObject);

        if (!optionalJSONObject.isPresent()) {
            //获取openid失败
            throw new GlobalSystemException(GlobalResponseCode.FAIL_GET_OPENID.getCode(),
                    GlobalResponseCode.FAIL_GET_OPENID.getMessage());
        }

        var data = jsonObject.getInnerMap();
        /// assert data.get("openid") != null;
        /// return (String) data.get("openid");
        assert data.get("openid") != null;
        String openid = (String) data.get("openid");
        if (openid == null) {
            throw new GlobalSystemException(GlobalResponseCode.FAIL_GET_OPENID.getCode(),
                    GlobalResponseCode.FAIL_GET_OPENID.getMessage());
        }
        return openid;
    }
}
