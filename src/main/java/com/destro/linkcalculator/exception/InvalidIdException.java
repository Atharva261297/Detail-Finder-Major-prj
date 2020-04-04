package com.destro.linkcalculator.exception;

public class InvalidIdException extends RuntimeException {

    private static final long serialVersionUID = 6439981160499647376L;

    public InvalidIdException(final String message) {
        super(message);
    }
}
