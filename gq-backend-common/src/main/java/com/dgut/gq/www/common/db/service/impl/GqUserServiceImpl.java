package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.entity.User;
import com.dgut.gq.www.common.db.mapper.UserMapper;
import com.dgut.gq.www.common.db.service.GqUserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GqUserServiceImpl
        extends ServiceImpl<UserMapper, User> implements GqUserService {
    @Override
    public User getByOpenid(String openid) {
        return lambdaQuery()
                .eq(User::getOpenid, openid)
                .one();
    }

    @Override
    public void updateRaceNumber(String openid) {
        lambdaUpdate()
                .setSql("race_number = race_number + 1")
                .eq(User::getOpenid, openid)
                .update();
    }

    @Override
    public List<User> getByOpenIds(List<String> openIds) {
        return lambdaQuery()
                .in(User::getOpenid, openIds)
                .list();
    }

    @Override
    public User getByUserName(String userName) {
        return lambdaQuery()
                .eq(User::getUserName, userName)
                .one();
    }
}
