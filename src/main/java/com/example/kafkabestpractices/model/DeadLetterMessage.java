package com.example.kafkabestpractices.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeadLetterMessage<E> {
    private String keyMessage;
    private E valueMessage;
}
