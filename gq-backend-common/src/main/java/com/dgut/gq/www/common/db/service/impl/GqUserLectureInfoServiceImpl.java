package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.common.db.entity.UserLectureInfo;
import com.dgut.gq.www.common.db.service.GqUserLectureInfoService;
import org.springframework.stereotype.Service;

@Service
public class GqUserLectureInfoServiceImpl
        extends ServiceImpl<UserLectureInfoMapper, UserLectureInfo> implements GqUserLectureInfoService {
}
