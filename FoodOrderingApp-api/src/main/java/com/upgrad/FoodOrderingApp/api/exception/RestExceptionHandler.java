package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class RestExceptionHandler {

    // Handle all exceptions raised during sign up
    @ExceptionHandler(SignUpRestrictedException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(SignUpRestrictedException exception, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(exception.getCode()).message(exception.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    // Handle all exceptions raised during authentication
    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> authenticationFailedException(AuthenticationFailedException authFailedException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(authFailedException.getCode()).message(authFailedException.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    // Handle all exceptions raised during authorization
    @ExceptionHandler(AuthorizationFailedException.class)
    public ResponseEntity<ErrorResponse> authorizationFailedException(AuthorizationFailedException authrFailedException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(authrFailedException.getCode()).message(authrFailedException.getErrorMessage()), HttpStatus.UNAUTHORIZED);
    }

    // Handle all exceptions raised during update of customer details
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException updateCustomerException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(updateCustomerException.getCode()).message(updateCustomerException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
}
