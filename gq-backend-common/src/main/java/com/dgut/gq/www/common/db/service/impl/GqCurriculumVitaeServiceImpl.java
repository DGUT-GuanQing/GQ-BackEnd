package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    @Override
    public Integer countByOpenId(String openid) {
        return lambdaQuery()
                .eq(CurriculumVitae::getOpenid, openid)
                .eq(CurriculumVitae::getIsDeleted, 0)
                .count();
    }

    @Override
    public CurriculumVitae getByOpenid(String openid) {
        return lambdaQuery()
                .eq(CurriculumVitae::getOpenid, openid)
                .eq(CurriculumVitae::getIsDeleted, 0)
                .one();
    }

    @Override
    public Page<CurriculumVitae> pageByDepartmentIdAndTerm(int page, int pageSize, String departmentId, Integer term) {
        LambdaQueryChainWrapper<CurriculumVitae> queryChainWrapper = lambdaQuery();
        return queryChainWrapper.eq(CurriculumVitae::getIsDeleted, 0)
                .orderByDesc(CurriculumVitae::getUpdateTime)
                .eq(departmentId != null && !departmentId.isEmpty(), CurriculumVitae::getDepartmentId, departmentId)
                .eq(term != null, CurriculumVitae::getTerm, term)
                .page(new Page<>(page, pageSize));

    }
}
