package com.example.aquascape.exception;

import com.example.aquascape.auth.AuthDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AuthDto.ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        
        String errorMessage = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        AuthDto.ErrorResponse error = AuthDto.ErrorResponse.builder()
                .message(errorMessage)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(org.springframework.web.multipart.support.MissingServletRequestPartException.class)
    public ResponseEntity<AuthDto.ErrorResponse> handleMissingServletRequestPartException(
            org.springframework.web.multipart.support.MissingServletRequestPartException ex) {
        
        AuthDto.ErrorResponse error = AuthDto.ErrorResponse.builder()
                .message("Required part '" + ex.getRequestPartName() + "' is not present.")
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .build();
                
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
