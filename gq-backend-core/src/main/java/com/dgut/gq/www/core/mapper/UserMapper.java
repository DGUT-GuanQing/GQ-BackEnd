package com.dgut.gq.www.core.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dgut.gq.www.common.db.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    public int robTicket(HashMap<String, String> map);
}
