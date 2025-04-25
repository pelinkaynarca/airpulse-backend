package com.airpulse.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetails> handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message("Resource Not Found")
                .detail(ex.getMessage())
                .build();
        return new ResponseEntity<>(problemDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ProblemDetails> handleBadRequest(Exception ex) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Bad Request")
                .detail("Invalid request format or missing/invalid parameters.")
                .build();
        return new ResponseEntity<>(problemDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleGenericException(Exception ex) {
        ProblemDetails problemDetails = ProblemDetails.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Internal Server Error")
                .detail("An unexpected error occurred. Please try again later.")
                .build();
        return new ResponseEntity<>(problemDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}