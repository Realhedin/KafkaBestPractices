package com.example.kafkabestpractices.config;

import com.example.kafkabestpractices.consumer.ConsumerWithDeadLetterRecoverer;
import com.example.kafkabestpractices.exceptions.ExceptionClassification;
import com.example.kafkabestpractices.exceptions.ExceptionClassifier;
import com.example.kafkabestpractices.model.DeadLetterMessage;
import com.example.kafkabestpractices.model.StudentEvent;
import com.example.kafkabestpractices.processor.StudentEventProcessor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
public class KafkaDeadLetterRecovererSpringConfiguration {

    @Value("${kafka.consumer.bootstrapServers}")
    private String bootstrapServers;

    @Bean
    public ConsumerWithDeadLetterRecoverer consumerWithDeadLetterRecoverer(
            KafkaTemplate<Long, DeadLetterMessage>  kafkaTemplate,
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


    @Bean
    public ProducerFactory<Long, DeadLetterMessage> producerFactory() {
        Map<String, Object> producerProps = new HashMap<>();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(producerProps);
    }

    @Bean
    public KafkaTemplate<Long, DeadLetterMessage>
        kafkaTemplate(ProducerFactory<Long, DeadLetterMessage> producerFactory) {
            return new KafkaTemplate<>(producerFactory);
    }

}
