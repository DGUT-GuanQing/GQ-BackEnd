package com.dgut.gq.www.core.common.mq;


/**
 * 消息投递失败时候获取openid和lectureId
 */
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@Data
@AllArgsConstructor
public class CustomCorrelationData extends CorrelationData {


    private final String openid;

    private final String lectureId;
}

