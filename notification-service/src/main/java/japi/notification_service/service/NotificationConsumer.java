package japi.notification_service.service;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Bean;
import lombok.extern.slf4j.Slf4j;
import japi.notification_service.dto.TransactionEvent;


@Service
@Slf4j
public class NotificationConsumer {

    @Bean
    public Consumer<TransactionEvent> consumeTransaction(){
        return event -> {
            log.info(" NOTIFICATION: User {} has done {} by {} {} ",
            event.userId(),
            event.type(),
            event.amount(),
            event.currency());

            if(event.amount().doubleValue() > 1000){
                log.warn("SECURITY ALERT: Higth transaction value detected");
            }
        };
    }

}
