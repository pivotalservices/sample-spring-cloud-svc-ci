package org.bk.producer.service;

import org.bk.producer.domain.Message;
import org.bk.producer.domain.MessageAcknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {
    private static final Logger logger = LoggerFactory.getLogger(MessageHandlerServiceImpl.class);


    private final String replyMessage;

    @Autowired
    public MessageHandlerServiceImpl(@Value("${reply.message}") String replyMessage) {
        this.replyMessage = replyMessage;
    }

    @Override
    public Mono<MessageAcknowledgement> handleMessage(Message message) {
        logger.info("About to Acknowledge");

        return Mono.delay(Duration.ofMillis(message.getDelayBy()))
                .map(l -> message.isThrowException())
                .map(throwException -> {
                    if (throwException) {
                        throw new RuntimeException("Throwing an exception!");
                    }
                    return new MessageAcknowledgement(message.getId(), message.getPayload(), this.replyMessage);
                });
    }


}