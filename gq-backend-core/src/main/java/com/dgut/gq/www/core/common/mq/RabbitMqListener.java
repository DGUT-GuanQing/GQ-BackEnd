package com.dgut.gq.www.core.common.mq;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.core.common.util.RecordRobTicketErrorUtil;
import com.dgut.gq.www.core.mapper.LectureMapper;
import com.dgut.gq.www.core.mapper.RecordRobTicketErrorMapper;
import com.dgut.gq.www.core.mapper.UserLectureInfoMapper;
import com.dgut.gq.www.core.common.model.entity.Lecture;
import com.dgut.gq.www.core.common.model.entity.UserLectureInfo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;


/**
 * rabnbitmq监听类
 * @author  hyj
 * @since  2022-12-15
 */
@Component
public class RabbitMqListener {

    @Autowired
    private UserLectureInfoMapper userLectureInfoMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private LectureMapper lectureMapper;

    @Autowired
    private RecordRobTicketErrorMapper recordRobTicketErrorMapper;

    /**
     * rabbitmq监听将抢票信息写入数据库
     */
    @RabbitListener(queues = "gq-rob-ticket-queue")
    @Transactional
    public  void robTicket(Message message, Channel channel) throws Exception {
        String str =  new String(message.getBody());
        UserLectureInfo userLectureInfo = JSONUtil.toBean(str,UserLectureInfo.class);
        String lectureId = userLectureInfo.getLectureId();
        String openid = userLectureInfo.getOpenid();
        String key = RedisGlobalKey.USER_MESSAGE + openid;

        try {
            boolean flag = robMsgIsInDb(userLectureInfo);
            if(!flag) {
                userLectureInfoMapper.insert(userLectureInfo);
                LambdaUpdateWrapper<Lecture> updateWrapper = new UpdateWrapper<Lecture>().lambda()
                        .setSql("ticket_number = ticket_number - 1")
                        .eq(Lecture::getId, lectureId)
                        .eq(Lecture::getIsDeleted,0)
                        .gt(Lecture::getTicketNumber, 0);
                lectureMapper.update(null, updateWrapper);
                stringRedisTemplate.delete(key);
            }

            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
        } catch (Exception e) {
            //如果异常尝试重新消费,两次过后记录报错
            String errorKey = RedisGlobalKey.CONSUME_FAIL + openid;
            str = stringRedisTemplate.opsForValue().get(key);
            int value = 1;
            if(str != null) {
                value = Integer.parseInt(str) + 1;
            }
            if(value >= 3){
                channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);
                RecordRobTicketErrorUtil.recordError(recordRobTicketErrorMapper, openid, lectureId, 2);
            }else {
                stringRedisTemplate.opsForValue().set(errorKey, String.valueOf(value));
                stringRedisTemplate.expire(key, 20, TimeUnit.MINUTES);
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), true, true);
            }
        }
    }

    private boolean robMsgIsInDb(UserLectureInfo userLectureInfo) {
        LambdaQueryWrapper<UserLectureInfo> userLectureInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLectureInfoLambdaQueryWrapper
                .eq(UserLectureInfo::getLectureId,userLectureInfo.getLectureId())
                .eq(UserLectureInfo::getOpenid,userLectureInfo.getOpenid())
                .eq(UserLectureInfo::getIsDeleted,0);
        Integer count = userLectureInfoMapper.selectCount(userLectureInfoLambdaQueryWrapper);
        return count > 0;
    }


}
