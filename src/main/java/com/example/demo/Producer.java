package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component

public class Producer {
    private Logger logger = LoggerFactory.getLogger(Producer.class);

    @RabbitListener(queues = "myqueue")
    public void process(String hello) {
       // System.out.println("Receiver1  : " + hello);
        logger.info("Receiver1  : " + hello);
    }//当没有消费者，消息存在queue中，等待消费者去取 

}

