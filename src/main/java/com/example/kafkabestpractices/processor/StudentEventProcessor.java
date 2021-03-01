package com.example.kafkabestpractices.processor;

import com.example.kafkabestpractices.model.Student;
import com.example.kafkabestpractices.model.StudentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;

@Slf4j
public class StudentEventProcessor {

    public void process(StudentEvent studentEvent, ConsumerRecord<Long, StudentEvent> consumerRecord) throws Exception {
        Student student = studentEvent.getStudent();
        Class<? extends Exception> exceptionClass = studentEvent.getExceptionClass();

        log.info("Started processing for student {} ", student.getName());
        if (exceptionClass == null) {
            log.info("Processing completed for Student {}", student.getName());
            return;
        }

        throw (exceptionClass.getDeclaredConstructor(String.class).newInstance("Throwing exception of type:"
                + exceptionClass.getName()));
    }
}
