package com.example.kafkabestpractices.config;

import com.example.kafkabestpractices.consumer.ConsumerWithDeadLetterRecoverer;
import com.example.kafkabestpractices.exceptions.ExceptionClassification;
import com.example.kafkabestpractices.exceptions.ExceptionClassifier;
import com.example.kafkabestpractices.processor.StudentEventProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@EnableKafka
public class KafkaDeadLetterRecovererSpringConfiguration {

    @Bean
    public ConsumerWithDeadLetterRecoverer consumerWithDeadLetterRecoverer(
            KafkaTemplate kafkaTemplate,
            ExceptionClassifier exceptionClassifier,
            StudentEventProcessor studentEventProcessor) {
        return ConsumerWithDeadLetterRecoverer
                .builder()
                .producerTemplate(kafkaTemplate)
                .studentEventProcessor(studentEventProcessor)
                .exceptionClassifier(exceptionClassifier)
                .build();
    }

    @Bean
    public StudentEventProcessor studentEventProcessor() {
        return new StudentEventProcessor();
    }

    @Bean
    public ExceptionClassifier exceptionClassifier() {
        Map<Class<? extends Throwable>, ExceptionClassification> classificationExceptions
                = Collections.unmodifiableMap(Stream.of(ExceptionClassification.values())
                .collect(Collectors.toMap(ExceptionClassification::getException, v -> v)));
        return new ExceptionClassifier(classificationExceptions, ExceptionClassification.UNCLASSIFIED_EXCEPTION);
    }



}
