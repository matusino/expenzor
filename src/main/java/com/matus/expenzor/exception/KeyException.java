package com.matus.expenzor.exception;

public class KeyException extends RuntimeException {
    public KeyException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

    public KeyException(String exMessage) {
        super(exMessage);
    }
}
