package com.example.demo;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * Created by Administrator on 2019/1/5 0005.
 * 注意 在 rabbit服务器上 将queue与exchange绑定
 */
@Controller
public class  Server{
    private Logger logger = LoggerFactory.getLogger(Server.class);
    @Autowired
    RabbitAdmin rabbitAdmin;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, "exchangemsxferror", "routingkeymsxferror");
    }


    @RequestMapping("/sendMq")
    @ResponseBody
    public String send(String name) throws Exception {
        String context = "hello " + name + " --" + new Date();
        String sendStr;
        for (int i = 1; i <= 100; i++) {
            sendStr = "第[" + i + "]个 hello  --" + new Date();
            logger.info("HelloSender: " + sendStr);
            sendMessage("myqueue", sendStr,i);
        }
        return context;
    }

    /**
     * 方式一：动态声明exchange和queue它们的绑定关系  rabbitAdmin
     *
     * @param exchangeName
     * @param queueName
     */
    protected void declareBinding(String exchangeName, String queueName,int id) {
        if (rabbitAdmin.getQueueProperties(queueName) == null) {
            /*  queue 队列声明
            durable=true,交换机持久化,rabbitmq服务重启交换机依然存在,保证不丢失; durable=false,相反
            auto-delete=true:无消费者时，队列自动删除; auto-delete=false：无消费者时，队列不会自动删除
            排他性，exclusive=true:首次申明的connection连接下可见; exclusive=false：所有connection连接下*/
            Queue queue = new Queue(queueName, true, false, false, null);
            rabbitAdmin.declareQueue(queue);
            TopicExchange directExchange = new TopicExchange(exchangeName);
            rabbitAdmin.declareExchange(directExchange);//声明exchange
            Binding binding = BindingBuilder.bind(queue).to(directExchange).with(queueName);    //将queue绑定到exchange
            rabbitAdmin.declareBinding(binding);      //声明绑定关系
        } else {
            rabbitAdmin.getRabbitTemplate().setQueue(queueName);
            rabbitAdmin.getRabbitTemplate().setExchange(queueName);
            rabbitAdmin.getRabbitTemplate().setRoutingKey(queueName);
            rabbitAdmin.getRabbitTemplate().setCorrelationKey(id+"");
        }
    }

    /**
     * 发送消息
     *
     * @param queueName
     * @param message
     * @throws Exception
     */
    public void sendMessage(String queueName, String message,int id) throws Exception {
        declareBinding(queueName, queueName,id);
        CorrelationData correlationData = new CorrelationData();
        correlationData.setId(id+"");
        rabbitAdmin.getRabbitTemplate().convertAndSend(queueName, queueName, message,correlationData);
    }
}
