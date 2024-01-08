package com.dgut.gq.www.core.common.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rabbit的配置类  绑定队列和交换机
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

    /**
     * 扫码签到交换机
     */
    public  static  final String GQ_SCAN_CHECKIN_EXCHANGE = "gq-scan-checkin-exchange";

    /**
     * 扫码签到队列
     */
    public  static  final String GQ_SCAN_CHECKIN_QUEUE = "gq-scan-checkin-queue";

    /**
     * 扫码签退交换机
     */
    public  static  final String GQ_SCAN_CHECKOUT_EXCHANGE = "gq-scan-checkout-exchange";

    /**
     * 扫码签退队列
     */
    public  static  final String GQ_SCAN_CHECKOUT_QUEUE = "gq-scan-checkout-queue";

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

}
