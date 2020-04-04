package com.destro.linkcalculator.exception;

public class InvalidTrainLinkFormatException extends RuntimeException {

    private static final long serialVersionUID = 1376535700942633963L;

    public InvalidTrainLinkFormatException(final String message) {
        super(message);
    }
}
