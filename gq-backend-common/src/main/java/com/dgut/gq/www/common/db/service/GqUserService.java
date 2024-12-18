package com.dgut.gq.www.common.db.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.User;

import java.util.List;

public interface GqUserService extends IService<User> {

    User getByOpenid(String openid);

    /**
     * 更新观看讲座记录
     * @param openid
     */
    void updateRaceNumber(String openid);

    List<User> getByOpenIds(List<String> openIds);

    User getByUserName(String userName);
}
