//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wisp.core.event.init;

import com.wisp.core.event.TaskEvent;
import com.wisp.core.utils.core.SpringContextHolder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.InitializingBean;

public class EventHander implements InitializingBean {
    private static RabbitTemplate rabbitTemplate;

    public EventHander() {
    }

    public static void publishEvent(TaskEvent event) {
        rabbitTemplate.convertAndSend(event.getMqQueue(), event);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate = SpringContextHolder.getBean(RabbitTemplate.class);
    }
}
