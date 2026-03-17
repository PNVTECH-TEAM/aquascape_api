package com.example.aquascape.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsecureFileException extends RuntimeException {
    public InsecureFileException(String message) {
        super(message);
    }
}
