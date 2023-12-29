package com.dgut.gq.www.admin.model.entity;

import lombok.Data;
import lombok.ToString;

/**
 * 封装token
 * @author hyj
 * @since 2022-9-16
 * @version 1.0
 */
@Data
@ToString
public class WeChatSession {
    private String openid;
    private String session_key;
}
