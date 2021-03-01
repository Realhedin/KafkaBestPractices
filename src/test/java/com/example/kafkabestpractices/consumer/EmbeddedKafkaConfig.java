package com.example.kafkabestpractices.consumer;

import com.example.kafkabestpractices.consumer.utils.IntegrationTestPublisher;
import com.example.kafkabestpractices.model.DeadLetterMessage;
import com.example.kafkabestpractices.model.Student;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

@TestConfiguration
//@Profile("integration-test")
public class EmbeddedKafkaConfig {
    @Value("${" + EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS + "}")
    private String brokerAddresses;

    @Value("${kafka.consumer.topic}")
    String STUDENT_EVENT_TOPIC;
    @Value("${kafka.producer.dltTopic}")
    String STUDENT_EVENT_DLT_TOPIC;
    @Value("${kafka.consumer.replayTopic}")
    String STUDENT_EVENT_REPLAY_TOPIC;

    @Bean("testStudentConsumer")
    public Consumer<Long, String> testStudentConsumer(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Consumer<Long, String> studentConsumer = getConsumer("consumerGroupId");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(studentConsumer, STUDENT_EVENT_TOPIC);
        return studentConsumer;
    }

    @Bean("testDeadLetterConsumer")
    public Consumer<Long, DeadLetterMessage<Student>> testDeadLetterConsumer(EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(brokerAddresses, "consumerDltGroupId", "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, DeadLetterMessage.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        DefaultKafkaConsumerFactory<Long, DeadLetterMessage<Student>> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        Consumer<Long, DeadLetterMessage<Student>> deadLetterMessageConsumer = defaultKafkaConsumerFactory.createConsumer("consumerDltGroupId", "IntegrationTest");
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(deadLetterMessageConsumer, STUDENT_EVENT_DLT_TOPIC);

        return deadLetterMessageConsumer;
    }

    private Consumer<Long, String> getConsumer(String consumerGroupId) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(brokerAddresses, consumerGroupId, "true");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<Long, String> defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        return defaultKafkaConsumerFactory.createConsumer(consumerGroupId, "IntegrationTest");
    }



    @Bean
    public IntegrationTestPublisher integrationTestPublisher() {
        Map<String, Object> producerProps = KafkaTestUtils.producerProps(brokerAddresses);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return IntegrationTestPublisher.builder()
                .integrationKafkaTemplate(new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producerProps)))
                .build();
    }

}
