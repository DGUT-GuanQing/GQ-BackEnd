package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.PositionMapper;
import com.dgut.gq.www.common.db.entity.Position;
import com.dgut.gq.www.common.db.service.GqPositionService;
import jodd.util.CollectionUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GqPositionServiceImpl
        extends ServiceImpl<PositionMapper, Position> implements GqPositionService {
    @Override
    public List<Position> getByDepartmentId(String departmentId) {
        return lambdaQuery()
                .eq(Position::getDepartmentId, departmentId)
                .eq(Position::getIsDeleted, 0)
                .list();
    }

    @Override
    public List<Position> getByIds(List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return lambdaQuery()
                .in(Position::getId, ids)
                .list();
    }
}
