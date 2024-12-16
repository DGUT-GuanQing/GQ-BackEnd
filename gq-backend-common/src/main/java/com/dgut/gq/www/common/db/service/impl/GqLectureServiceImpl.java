package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.LectureMapper;
import com.dgut.gq.www.common.db.entity.Lecture;
import com.dgut.gq.www.common.db.service.GqLectureService;
import org.springframework.stereotype.Service;

@Service
public class GqLectureServiceImpl
        extends ServiceImpl<LectureMapper, Lecture> implements GqLectureService {
}
