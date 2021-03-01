package com.example.kafkabestpractices.consumer.utils;

import com.example.kafkabestpractices.model.Student;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.DeserializationException;

public class ErrorHandlerTestingUtils {


    public static String getSendingMessageAsString(Student student,
                                                   Class<? extends Exception> exceptionClass) {
        try {
            if (exceptionClass == DeserializationException.class) {
                return JacksonUtils.enhancedObjectMapper().writeValueAsString("deserializationException");
            }
            return JacksonUtils.enhancedObjectMapper().writeValueAsString(student);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
