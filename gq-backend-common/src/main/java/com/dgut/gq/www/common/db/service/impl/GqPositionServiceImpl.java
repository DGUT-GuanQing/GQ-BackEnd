package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.PositionMapper;
import com.dgut.gq.www.common.db.entity.Position;
import com.dgut.gq.www.common.db.service.GqPositionService;
import org.springframework.stereotype.Service;

@Service
public class GqPositionServiceImpl
        extends ServiceImpl<PositionMapper, Position> implements GqPositionService {
}
