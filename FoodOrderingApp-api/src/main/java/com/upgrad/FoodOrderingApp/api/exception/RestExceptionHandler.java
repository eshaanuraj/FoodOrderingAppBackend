package com.upgrad.FoodOrderingApp.api.exception;

import com.upgrad.FoodOrderingApp.api.model.ErrorResponse;
import com.upgrad.FoodOrderingApp.service.exception.*;
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
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(authrFailedException.getCode()).message(authrFailedException.getErrorMessage()), HttpStatus.FORBIDDEN);
    }

    // Handle all exceptions raised during update of customer details
    @ExceptionHandler(UpdateCustomerException.class)
    public ResponseEntity<ErrorResponse> updateCustomerException(UpdateCustomerException updateCustomerException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(updateCustomerException.getCode()).message(updateCustomerException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }

    
    // Handle Exceptions related to Category API's
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> categoryNotFoundException(CategoryNotFoundException categoryNotFoundException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(categoryNotFoundException.getCode()).message(categoryNotFoundException.getErrorMessage()), HttpStatus.NOT_FOUND);
    }
    

    // Handle Exceptions related to Address API's
    @ExceptionHandler(SaveAddressException.class)
    public ResponseEntity<ErrorResponse> saveAddressException(SaveAddressException saveAddressException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(saveAddressException.getCode()).message(saveAddressException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
    

    // Handle Exceptions related to Address API's
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ErrorResponse> addressNotFoundException(AddressNotFoundException addressNotFoundException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(addressNotFoundException.getCode()).message(addressNotFoundException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
    
    
    // Handle Exceptions related to Order API's
    @ExceptionHandler(CouponNotFoundException.class)
    public ResponseEntity<ErrorResponse> couponNotFoundException(CouponNotFoundException couponNotFoundException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(couponNotFoundException.getCode()).message(couponNotFoundException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
    
    
    // Handle Exceptions related to Order API's
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ErrorResponse> restaurantNotFoundException(RestaurantNotFoundException restaurantNotFoundException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(restaurantNotFoundException.getCode()).message(restaurantNotFoundException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
    

    // Handle Exceptions related to Order API's
    @ExceptionHandler(PaymentMethodNotFoundException.class)
    public ResponseEntity<ErrorResponse> paymentMethodNotFoundException(PaymentMethodNotFoundException paymentMethodNotFoundException, WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse().code(paymentMethodNotFoundException.getCode()).message(paymentMethodNotFoundException.getErrorMessage()), HttpStatus.BAD_REQUEST);
    }
    // Handle Exceptions related to Restaurant API's
    @ExceptionHandler(InvalidRatingException.class)
    public ResponseEntity<ErrorResponse> invalidRatingException(InvalidRatingException exc ,WebRequest request) {
        return new ResponseEntity<ErrorResponse>(new ErrorResponse()
                .code(exc.getCode())
                .message(exc.getErrorMessage()),
                HttpStatus.BAD_REQUEST);
    }

}
