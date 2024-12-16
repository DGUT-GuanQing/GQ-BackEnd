package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.entity.User;
import com.dgut.gq.www.common.db.mapper.UserMapper;
import com.dgut.gq.www.common.db.service.GqUserService;
import org.springframework.stereotype.Service;

@Service
public class GqUserServiceImpl
        extends ServiceImpl<UserMapper, User> implements GqUserService {
}
