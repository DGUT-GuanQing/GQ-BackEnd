package com.dgut.gq.www.common.db.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dgut.gq.www.common.db.mapper.RecordRobTicketErrorMapper;
import com.dgut.gq.www.common.db.entity.RecordRobTicketError;
import com.dgut.gq.www.common.db.service.GqRecordRobTicketErrorService;
import org.springframework.stereotype.Service;

@Service
public class GqRecordRobTicketErrorServiceImpl
        extends ServiceImpl<RecordRobTicketErrorMapper, RecordRobTicketError> implements GqRecordRobTicketErrorService {
}
