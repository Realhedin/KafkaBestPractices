package com.example.kafkabestpractices.exceptions;

import org.springframework.classify.SubclassClassifier;
import org.springframework.http.HttpStatus;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExceptionClassifier extends SubclassClassifier<Throwable, ExceptionClassification> {
    private static final Set<HttpStatus> RETRYABLE_HTTP_CODES = Stream.of(
            HttpStatus.SERVICE_UNAVAILABLE,
            HttpStatus.GATEWAY_TIMEOUT,
            HttpStatus.BAD_GATEWAY)
            .collect(Collectors.toSet());

    public ExceptionClassifier(Map<Class<? extends Throwable>, ExceptionClassification> typeMap, ExceptionClassification defaultValue) {
        super(typeMap, defaultValue);
    }

    @Override
    public ExceptionClassification classify(Throwable classifiable) {
        ExceptionClassification classification = super.classify(classifiable);

        if (classification.equals(this.getDefault())) {
            Throwable cause = classifiable;

            do {
                if (this.getClassified().containsKey(cause.getClass())) {
                    return classification; //non-default classification
                }
                cause = cause.getCause();
                classification = super.classify(cause);
            }
            while (cause != null && classification.equals(this.getDefault()));
        }

        return classification;
    }
}
