package com.complaints.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UnableToModifyException extends RuntimeException {
    public UnableToModifyException() {
    }

    public UnableToModifyException(String message) {
        super(message);
    }

    public UnableToModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnableToModifyException(Throwable cause) {
        super(cause);
    }
}
