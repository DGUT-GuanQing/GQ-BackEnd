package com.dgut.gq.www.common.db.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;
import com.dgut.gq.www.common.db.service.GqUserLectureInfoService;
import jodd.util.CollectionUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GqUserLectureInfoServiceImpl
        extends ServiceImpl<UserLectureInfoMapper, UserLectureInfo> implements GqUserLectureInfoService {

    @Override
    public Page<UserLectureInfo> getByOpenid(String openid, Integer page, Integer pageSize) {
        return  lambdaQuery()
                .eq(UserLectureInfo::getOpenid, openid)
                .orderByDesc(UserLectureInfo::getCreateTime)
                .eq(UserLectureInfo::getIsDeleted, 0)
                .page(new Page<>(page, pageSize));
    }

    @Override
    public UserLectureInfo getByLectureAndOpenid(String lectureId, String openid) {
        return lambdaQuery()
                .eq(UserLectureInfo::getLectureId, lectureId)
                .eq(UserLectureInfo::getOpenid, openid)
                .eq(UserLectureInfo::getIsDeleted, 0)
                .one();
    }

    @Override
    public Page<UserLectureInfo> getByLectureId(int page, int pageSize, String id, Integer status) {
        LambdaQueryChainWrapper<UserLectureInfo> lectureInfoChainWrapper = lambdaQuery();
        lectureInfoChainWrapper.eq(UserLectureInfo::getIsDeleted, 0);
        lectureInfoChainWrapper.eq(UserLectureInfo::getLectureId, id);
        //如果是1查询参加讲座的
        if (status == 1) {
            lectureInfoChainWrapper.ge(UserLectureInfo::getStatus, status);
        }
        return lambdaQuery().page(new Page<>(page, pageSize));
    }

    @Override
    public List<UserLectureInfo> getByLectureId(String id, Integer status) {
        LambdaQueryChainWrapper<UserLectureInfo> lambdaQueryChainWrapper =  lambdaQuery();
        //如果是1查询参加讲座的
        if (status == 1) {
            lambdaQueryChainWrapper.ge(UserLectureInfo::getStatus, 1);
        }
        return lambdaQueryChainWrapper
               .eq(UserLectureInfo::getLectureId, id)
               .eq(UserLectureInfo::getIsDeleted, 0)
               .list();
    }

}
