package com.dgut.gq.www.core.common.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.gq.www.core.common.model.entity.RecordRobTicketError;
import com.dgut.gq.www.core.mapper.RecordRobTicketErrorMapper;

import java.time.LocalDateTime;

public class RecordRobTicketErrorUtil {


    public static void recordError(RecordRobTicketErrorMapper recordRobTicketErrorMapper, String openid, String lectureId, int type) {
        //已经保存了就不要重复记录
        LambdaQueryWrapper<com.dgut.gq.www.core.common.model.entity.RecordRobTicketError> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(RecordRobTicketError::getOpenid,openid)
                .eq(RecordRobTicketError::getLectureId,lectureId)
                .eq(RecordRobTicketError::getType,type);
        Integer count = recordRobTicketErrorMapper.selectCount(lambdaQueryWrapper);
        if(count > 0)return;

        com.dgut.gq.www.core.common.model.entity.RecordRobTicketError recordRobTicketError = new com.dgut.gq.www.core.common.model.entity.RecordRobTicketError();
        recordRobTicketError.setCreateTime(LocalDateTime.now());
        recordRobTicketError.setUpdateTime(LocalDateTime.now());
        recordRobTicketError.setOpenid(openid);
        recordRobTicketError.setLectureId(lectureId);
        recordRobTicketError.setType(type);
        recordRobTicketErrorMapper.insert(recordRobTicketError);
    }
}
