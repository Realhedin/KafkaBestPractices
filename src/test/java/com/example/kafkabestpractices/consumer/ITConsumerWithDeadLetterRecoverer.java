package com.example.kafkabestpractices.consumer;

import com.example.kafkabestpractices.KafkaBestPracticesApplication;
import com.example.kafkabestpractices.consumer.utils.ErrorHandlerTestingUtils;
import com.example.kafkabestpractices.consumer.utils.IntegrationTestPublisher;
import com.example.kafkabestpractices.model.DeadLetterMessage;
import com.example.kafkabestpractices.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
//@ActiveProfiles("integration-tests")
@Import(EmbeddedKafkaConfig.class)
@SpringBootTest(classes = KafkaBestPracticesApplication.class)
@TestPropertySource(properties = "spring.config.location=classpath:application-test.yml")
@EmbeddedKafka(partitions = 1, topics = {"${kafka.consumer.topic}", "${kafka.producer.dltTopic}"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ITConsumerWithDeadLetterRecoverer {

    @Autowired
    Consumer<Long, String> testStudentConsumer;
    @Autowired
    Consumer<Long, DeadLetterMessage<Student>> testDeadLetterConsumer;

    @Autowired
    private ConsumerWithDeadLetterRecoverer consumerWithDeadLetterRecoverer;

    @Autowired
    IntegrationTestPublisher integrationTestPublisher;

    @Value("${kafka.consumer.topic}")
    String kafkaTopic;

    @Test
    public void postitiveTest() {
        Long key = 1L;
        integrationTestPublisher.produce(kafkaTopic,key,
                ErrorHandlerTestingUtils.getSendingMessageAsString(Student.builder().name("Bob").build(),null));
        ConsumerRecords<Long, String> consumerRecords = KafkaTestUtils.getRecords(testStudentConsumer, 5000,1);
        Assertions.assertEquals(1, consumerRecords.count());
        ConsumerRecords<Long, DeadLetterMessage<Student>> dltRecords = KafkaTestUtils.getRecords(testDeadLetterConsumer, 5000,1);
        Assertions.assertEquals(0, dltRecords.count());

    }

}