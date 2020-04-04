package com.destro.linkcalculator.exception;

public class InvalidNameUpdate extends RuntimeException {
    private static final long serialVersionUID = -6996834071056045941L;

    public InvalidNameUpdate(final String message) {
        super(message);
    }
}
