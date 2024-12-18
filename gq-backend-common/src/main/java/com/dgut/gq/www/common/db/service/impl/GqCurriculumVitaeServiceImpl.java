package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.entity.CurriculumVitae;
import com.dgut.gq.www.common.db.mapper.CurriculumVitaeMapper;
import com.dgut.gq.www.common.db.service.GqCurriculumVitaeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GqCurriculumVitaeServiceImpl extends
        ServiceImpl<CurriculumVitaeMapper, CurriculumVitae> implements GqCurriculumVitaeService {

    @Override
    public List<CurriculumVitae> getByDepartmentIdAndTerm(String departmentId, Integer term) {
        return lambdaQuery()
                .eq(CurriculumVitae::getIsDeleted, 0)
                .orderByDesc(CurriculumVitae::getUpdateTime)
                .eq(departmentId != null && !departmentId.isEmpty(), CurriculumVitae::getDepartmentId, departmentId)
                .eq(term != null, CurriculumVitae::getTerm, term)
                .list();
    }
}
