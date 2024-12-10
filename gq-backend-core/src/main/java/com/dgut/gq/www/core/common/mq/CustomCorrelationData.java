package com.dgut.gq.www.core.common.mq;



import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * 消息投递失败时候获取openid和lectureId
 *
 * @author hyj
 * @version 1.0
 * @since 2022-9-16
 */
@Data
@AllArgsConstructor
public class CustomCorrelationData extends CorrelationData {


    private final String openid;

    private final String lectureId;
}

