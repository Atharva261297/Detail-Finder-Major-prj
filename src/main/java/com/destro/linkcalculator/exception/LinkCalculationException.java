package com.destro.linkcalculator.exception;

public class LinkCalculationException extends RuntimeException {

    private static final long serialVersionUID = -389007604083946881L;

    public LinkCalculationException(final String message) {
        super(message);
    }

    public LinkCalculationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
