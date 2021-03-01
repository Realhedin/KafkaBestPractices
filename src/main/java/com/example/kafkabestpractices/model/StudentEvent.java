package com.example.kafkabestpractices.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentEvent {
    private String changeId;
    private String eventType;

    private Student student;
    private Class<? extends Exception> exceptionClass;
}
