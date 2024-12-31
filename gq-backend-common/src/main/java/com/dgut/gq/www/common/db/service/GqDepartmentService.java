package com.dgut.gq.www.common.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.Department;

import java.util.List;

public interface GqDepartmentService extends IService<Department> {

    List<Department> getAll();

    List<Department> getByIds(List<String> ids);
}
