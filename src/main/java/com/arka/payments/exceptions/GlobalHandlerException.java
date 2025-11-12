package com.arka.payments.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(InvalidPaymentException.class)
    public ResponseEntity<String>handleInvalidPaymentException(InvalidPaymentException exception){
        return new ResponseEntity<>("Error"+exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(OrderException.class)
    public ResponseEntity<String>handleOrderException(OrderException exception){
        return new ResponseEntity<>("Error"+exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PaymentFailedException.class)
    public ResponseEntity<String>handlePaymentFailedException(PaymentFailedException exception){
        return new ResponseEntity<>("Error"+exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PaymentNotFound.class)
    public ResponseEntity<String>handlePaymentNotFound(PaymentNotFound exception){
        return new ResponseEntity<>("Error"+exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = "Validation Fail: " + Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllUncaughtException(Exception ex) {
        String message = ex.getMessage();
        if (message != null && (
                message.contains("favicon.ico") ||
                        message.contains("swagger") ||
                        message.contains("webjars") ||
                        message.contains("api-docs")
        )) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>("Internal Error. Try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
