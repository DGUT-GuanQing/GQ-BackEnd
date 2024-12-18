package com.dgut.gq.www.common.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.CurriculumVitae;

import java.util.List;

public interface GqCurriculumVitaeService extends IService<CurriculumVitae> {

    List<CurriculumVitae> getByDepartmentIdAndTerm(String departmentId, Integer term);
}
