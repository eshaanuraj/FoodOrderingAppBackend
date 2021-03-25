package com.upgrad.FoodOrderingApp.api.controller;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveOrderRequest;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.CommonService;
import com.upgrad.FoodOrderingApp.service.businness.CouponService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

@RestController
public class OrderController {

	@Autowired
	private AuthenticationService authenticationService;
	    

	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CouponService couponService;
	
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByCouponName(@RequestHeader("authorization") final String authorization,
    		@RequestParam("coupon_name") String couponName) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException, CouponNotFoundException {


        String[] splitText = authorization.split("Basic "); 
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        
        if (decodedTextArray.length == 2) {
            CustomerAuthTokenEntity customerAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);
            
            if(customerAuthToken == null) {
            	throw new AuthorizationFailedException("ATHR-001","Customer is not Logged in"); 
            }
            if(customerAuthToken.getLogoutAt() != null) {
            	throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
            } 
            if(customerAuthToken.getExpiresAt() != null) {
            	throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint."); 
            }
            
            CouponEntity couponByName = couponService.getCouponByName(couponName);  
            CouponDetailsResponse response = new CouponDetailsResponse();
            response.setId(couponByName.getUuid());  
            response.setCouponName(couponByName.getCouponName()); 
            response.setPercent(couponByName.getPercent()); 
            return new ResponseEntity<CouponDetailsResponse>(response,HttpStatus.OK);
        }
        
    	return null;
    	
    }
    
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AddressEntity>> getPastOrdersOfUser(@RequestHeader("authorization") final String authorization) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException {

    	return null;
    	
    }
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AddressEntity>> saveOrder(@RequestHeader("authorization") final String authorization,
    		SaveOrderRequest saveOrderRequest) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException {

    	return null;
    	
    }
    
}
