package com.dgut.gq.www.common.db.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.mapper.LectureMapper;
import com.dgut.gq.www.common.db.service.GqLectureService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class GqLectureServiceImpl extends ServiceImpl<LectureMapper, Lecture> implements GqLectureService {

    @Override
    public Lecture getLatestUnStartLecture() {
        return lambdaQuery().eq(Lecture::getIsDeleted, 0).orderByDesc(Lecture::getCreateTime).last("limit 1").one();
    }

    @Override
    public Page<Lecture> getLectures(int page, int pageSize, String msg, int type) {
        if (page < 0 || pageSize < 0) {
            return new Page<>();
        }
        LambdaQueryChainWrapper<Lecture> lectureLambdaQueryChainWrapper = lambdaQuery();
        Optional.ofNullable(msg)
                .ifPresent(n -> lectureLambdaQueryChainWrapper
                        .and(wrapper -> wrapper
                                .like(Lecture::getGuestName, msg)
                                .or()
                                .like(Lecture::getLectureName, msg)
                        ));
        return lectureLambdaQueryChainWrapper
                .orderByDesc(Lecture::getCreateTime)
                .eq(Lecture::getIsDeleted, 0)
                .ne(type == 0, Lecture::getReviewName, "")
                .page(new Page<>(page, pageSize));
    }

    @Override
    public List<Lecture> getByIds(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        String s = StrUtil.join(",", list);
        return lambdaQuery()
                .in(Lecture::getId, list)
                .eq(Lecture::getIsDeleted, 0)
                .last("ORDER BY FIELD(id," + s + ")")
                .list();
    }
}
