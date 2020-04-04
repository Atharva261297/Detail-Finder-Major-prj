package com.destro.linkcalculator.exception;

public class DaoException extends RuntimeException {

    private static final long serialVersionUID = -4274722500922393648L;

    public DaoException(final String message) {
        super(message);
    }

    public DaoException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
