package com.example.kafkabestpractices.consumer.utils;

import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.TimeUnit;

@Slf4j
@Builder
public class IntegrationTestPublisher {

    @NonNull
    private final KafkaTemplate<Long, String> integrationKafkaTemplate;

    public void produce(String topic, Long key, String message) {
        try {
            SendResult<Long, String> sendResult = integrationKafkaTemplate.send(topic, key, message).get(3, TimeUnit.SECONDS);
            log.info("Test Input published Topic:{} with Key: {}, value:{}", topic, sendResult.getProducerRecord().key(), sendResult.getProducerRecord().value());
        } catch (Exception e) {
            log.error("Exception");
        }
    }
}
