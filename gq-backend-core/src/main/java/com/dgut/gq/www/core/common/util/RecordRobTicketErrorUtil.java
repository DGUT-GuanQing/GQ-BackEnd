package com.dgut.gq.www.core.common.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dgut.gq.www.common.db.entity.RecordRobTicketError;
import com.dgut.gq.www.common.db.mapper.RecordRobTicketErrorMapper;


import java.time.LocalDateTime;

public class RecordRobTicketErrorUtil {

    public static void recordError(RecordRobTicketErrorMapper recordRobTicketErrorMapper, String openid, String lectureId, int type) {
        //已经保存了就不要重复记录
        LambdaQueryWrapper<RecordRobTicketError> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(RecordRobTicketError::getOpenid, openid)
                .eq(RecordRobTicketError::getLectureId, lectureId)
                .eq(RecordRobTicketError::getType, type);
        Integer count = recordRobTicketErrorMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            return;
        }
        RecordRobTicketError recordRobTicketError = new RecordRobTicketError();
        recordRobTicketError.setCreateTime(LocalDateTime.now());
        recordRobTicketError.setUpdateTime(LocalDateTime.now());
        recordRobTicketError.setOpenid(openid);
        recordRobTicketError.setLectureId(lectureId);
        recordRobTicketError.setType(type);
        recordRobTicketErrorMapper.insert(recordRobTicketError);
    }
}
