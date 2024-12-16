package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.CurriculumVitaeMapper;
import com.dgut.gq.www.common.db.entity.CurriculumVitae;
import com.dgut.gq.www.common.db.service.GqCurriculumVitaeService;
import org.springframework.stereotype.Service;

@Service
public class GqCurriculumVitaeServiceImpl extends
        ServiceImpl<CurriculumVitaeMapper, CurriculumVitae> implements GqCurriculumVitaeService {
}
