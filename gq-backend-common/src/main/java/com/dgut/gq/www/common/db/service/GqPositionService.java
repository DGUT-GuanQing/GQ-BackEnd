package com.dgut.gq.www.common.db.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dgut.gq.www.common.db.entity.Position;

import java.util.List;

public interface GqPositionService extends IService<Position> {

    List<Position> getByDepartmentId(String departmentId);
}
