package com.example.demo;


import com.example.demo.com.example.entity.Order;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component

public class Producer {
    private Logger logger = LoggerFactory.getLogger(Producer.class);

    @RabbitListener(queues = "myqueue")
    @RabbitHandler
    public void process(String hello) {
       // System.out.println("Receiver1  : " + hello);
        logger.info("Receiver1  : " + hello);
        //没有自动签收（需要到配置端修改   spring.rabbitmq.listener.simple.acknowledge-mode=auto
    }//当没有消费者，消息存在queue中，等待消费者去取

    /**
     *
     * @param order 消息体，可以传递对象
     * @param Headers 消息头
     * @param channel 信道
     *      durable ="true"          //是否序列号
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value="queue_test",durable ="true"),
            exchange = @Exchange(name="exchange_test",durable = "true",type = "topic"),
            key = "order.*"

    ))
    @RabbitHandler
    public void process1(@Payload Order order, @Headers Map<String,Object> Headers, Channel channel) throws Exception{
        // System.out.println("Receiver1  : " + hello);
        logger.info("Receiver1  __id: " + order.getId());
        long delivery_tag = (long)Headers.get(AmqpHeaders.DELIVERY_TAG);
        //由于配置文件设置的手动签收，所以需要调用
        channel.basicAck(delivery_tag,false);//false代表不支持批量签收//手动签收，签收代表：收到一条消息然后给服务端一个响应，（一般这样做）
    }
}

