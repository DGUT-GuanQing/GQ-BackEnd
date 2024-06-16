package com.dgut.gq.www.core.common.config;

import com.dgut.gq.www.core.mapper.RecordRobTicketErrorMapper;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * rabbit的配置类
 * @author hyj
 * @since  2022-12-15
 */
@Configuration
public class RabbitmqConfig {

    /**
     * 抢票的交换机
     */
    public  static  final String GQ_ROB_TICKET_EXCHANGE = "gq-rob-ticket-exchange";

    /**
     * 抢票的队列
     */
    public  static  final String GQ_ROB_TICKET_QUEUE = "gq-rob-ticket-queue";

    @Autowired
    private RecordRobTicketErrorMapper recordRobTicketErrorMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 抢票交换机
     * @return
     */
    @Bean(name = "gq-rob-ticket-exchange")
    public Exchange bootExchange1(){
        return ExchangeBuilder.topicExchange(GQ_ROB_TICKET_EXCHANGE).durable(true).build();
    }

    /**
     * 抢票队列
     */
    @Bean(name = "gq-rob-ticket-queue")
    public Queue bootQueue1(){
      return QueueBuilder.durable(GQ_ROB_TICKET_QUEUE).build();
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindQueueExchange1(@Qualifier("gq-rob-ticket-queue") Queue queue , @Qualifier("gq-rob-ticket-exchange") Exchange exchange){
       return BindingBuilder.bind(queue).to(exchange).with("gqyyds").noargs();
    }

    /*@Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        // 消息确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            *//**
             * MQ确认回调方法
             * @param correlationData 消息的唯一标识
             * @param ack 消息是否成功收到
             * @param cause 失败原因
             *//*
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    String openid;
                    String lectureId;
                    if (correlationData instanceof CustomCorrelationData) {
                        CustomCorrelationData customData = (CustomCorrelationData) correlationData;
                        openid = customData.getOpenid();
                        lectureId = customData.getLectureId();
                        String key = RedisGlobalKey.SEND_EXCHANGE_FAIL + openid;
                        String str = stringRedisTemplate.opsForValue().get(key);

                        //如果异常尝试重新投递消息,两次过后记录报错
                        int value = 1;
                        if(str != null) {
                            value = Integer.parseInt(str) + 1;
                        }
                        if(value >= 3){
                            RecordRobTicketErrorUtil.recordError(recordRobTicketErrorMapper,openid,lectureId,0);
                        }else {
                            stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
                            stringRedisTemplate.expire(key, 20, TimeUnit.MINUTES);
                            RetrySendMsg(rabbitTemplate, openid, lectureId);
                        }
                    }
                }
            }
        });

        return rabbitTemplate;
    }

    private void RetrySendMsg(RabbitTemplate rabbitTemplate, String openid, String lectureId) {
        UserLectureInfo userLectureInfo = buildUserLectureInfo(openid,lectureId);
        CustomCorrelationData correlationData = new CustomCorrelationData(openid, lectureId);

        rabbitTemplate.convertAndSend(RabbitmqConfig.GQ_ROB_TICKET_EXCHANGE,"gqyyds", JSONUtil.toJsonStr(userLectureInfo),correlationData);
    }
    private UserLectureInfo buildUserLectureInfo(String openid, String lectureId) {
        UserLectureInfo userLectureInfo = new UserLectureInfo();
        userLectureInfo.setUpdateTime(LocalDateTime.now());
        userLectureInfo.setCreateTime(LocalDateTime.now());
        userLectureInfo.setLectureId(lectureId);
        userLectureInfo.setOpenid(openid);
        return  userLectureInfo;
    }*/
}
