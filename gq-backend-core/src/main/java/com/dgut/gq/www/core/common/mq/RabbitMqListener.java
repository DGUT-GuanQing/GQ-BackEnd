package com.dgut.gq.www.core.common.mq;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dgut.gq.www.common.common.RedisGlobalKey;
import com.dgut.gq.www.core.mapper.LectureMapper;
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


/**
 * rabnbitmq监听类
 * @author  hyj
 * @since  2022-12-15
 */
@Component
public class RabbitMqListener {

    @Autowired
    private UserLectureInfoMapper infoMapper;


    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private LectureMapper lectureMapper;


    /**
     * rabbitmq监听将抢票信息写入数据库
     */
    @RabbitListener(queues = "gq-rob-ticket-queue")
    @Transactional
    public  void robTicket(Message message, Channel channel) throws Exception {
        String str ;
        String key = RedisGlobalKey.USER_MESSAGE;
        try {
            //接受消息
             str =  new String(message.getBody());
            //转换成讲座用户信息
            UserLectureInfo userLectureInfo = JSONUtil.toBean(str,UserLectureInfo.class);
            //将用户讲座表插入数据库
            infoMapper.insert(userLectureInfo);
            //删除我的信息
            stringRedisTemplate.delete(key + userLectureInfo.getOpenid());

            //更新讲座信息并且写入数据库
            LambdaUpdateWrapper<Lecture> updateWrapper = new UpdateWrapper<Lecture>().lambda()
                    .setSql("ticket_number = ticket_number - 1")
                    .eq(Lecture::getId, userLectureInfo.getLectureId())
                    .gt(Lecture::getTicketNumber, 0);
            lectureMapper.update(null,updateWrapper);
            //消息队列签收信息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),true);

        } catch (Exception e) {
            //如果发送异常  消息重新回队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),true,true);
            throw new RuntimeException(e);
        }
    }


}
