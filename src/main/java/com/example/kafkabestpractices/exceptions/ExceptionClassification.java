package com.example.kafkabestpractices.exceptions;

import com.example.kafkabestpractices.exceptions.types.UnClassifiedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.naming.ServiceUnavailableException;
import java.sql.SQLTransientException;

import static com.example.kafkabestpractices.exceptions.ExceptionType.TRANSIENT_EXCEPTION_TYPE;
import static com.example.kafkabestpractices.exceptions.ExceptionType.UNCLASSIFIED_EXCEPTION_TYPE;

@Getter
@AllArgsConstructor
public enum ExceptionClassification {
    SQL_TRANSIENT_EXCEPTION(SQLTransientException.class, TRANSIENT_EXCEPTION_TYPE, true),
    SERVICE_UNAVAILABLE_EXCEPTION(ServiceUnavailableException.class, TRANSIENT_EXCEPTION_TYPE, true),

    UNCLASSIFIED_EXCEPTION(UnClassifiedException.class, UNCLASSIFIED_EXCEPTION_TYPE, false);


    private Class<? extends Exception> exception;
    private ExceptionType exceptionType;
    private boolean replayable;

}
