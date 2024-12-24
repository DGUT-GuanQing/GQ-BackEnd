package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.DepartmentMapper;
import com.dgut.gq.www.common.db.entity.Department;
import com.dgut.gq.www.common.db.service.GqDepartmentService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class GqDepartmentServiceImpl extends
        ServiceImpl<DepartmentMapper, Department> implements GqDepartmentService {
    @Override
    public List<Department> getAll() {
        return lambdaQuery()
                .eq(Department::getIsDeleted, 0)
                .list();
    }
}
