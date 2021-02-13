package com.posicube.robi.reception.exception;

public class CsvFileHandlingException extends RuntimeException{

    public CsvFileHandlingException(String reason, Throwable throwable) {
        super(reason, throwable);
    }
}
