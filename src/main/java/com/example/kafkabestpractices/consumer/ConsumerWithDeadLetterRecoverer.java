package com.example.kafkabestpractices.consumer;

import com.example.kafkabestpractices.exceptions.ExceptionClassifier;
import com.example.kafkabestpractices.exceptions.ExceptionType;
import com.example.kafkabestpractices.model.StudentEvent;
import com.example.kafkabestpractices.processor.StudentEventProcessor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;

@Slf4j
@Builder
public class ConsumerWithDeadLetterRecoverer extends AbstractConsumerSeekAware {
    private final KafkaTemplate<Long, StudentEvent> producerTemplate;
    ExceptionClassifier exceptionClassifier;
    StudentEventProcessor studentEventProcessor;

    @Value("${kafka.consumer.topic}")
    String topic;

    @Value("${kafka.consumer.groupId}")
    String groupId;

    @KafkaListener(
            topics = {"${kafka.consumer.topic}", "${kafka.consumer.replayTopic}"},
            groupId = "${kafka.consumer.groupId}"
    )
    public void consume(ConsumerRecord<Long, StudentEvent> consumerRecord) throws Exception {
        StudentEvent studentEvent = consumerRecord.value();
        log.info("Consumed message for Student {}", studentEvent.getStudent().getName());

        try {
            studentEventProcessor.process(studentEvent, consumerRecord);
            //post processing - if needed
            //producerTemplate.send(topic, studentEvent.getStudent().getName(), studentEvent);
            log.info("Successfully consumed message for Student {}", studentEvent.getStudent().getName());
        } catch (Exception exception) {
            log.error("Exception processing message", exception);
            if (exceptionClassifier.classify(exception).getExceptionType() == ExceptionType.TRANSIENT_EXCEPTION_TYPE) {
                //stop the world exceptions. Do not proceed until resolved.
                log.error("Received transient resource exception, do not proceed, keep retrying..", exception);

                throw exception;
            }
        }
    }


}
