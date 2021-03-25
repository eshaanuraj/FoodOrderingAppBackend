package com.upgrad.FoodOrderingApp.api.controller;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;

@RestController
public class AddressController {

    @Autowired
    private AuthenticationService authenticationService;
    
    
    @Autowired
    private AddressService addressService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") final String authorization,
    		final SaveAddressRequest saveAddressRequest) throws SignUpRestrictedException, AuthenticationFailedException, AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
      
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
            
            CustomerEntity customerEntity = customerAuthToken.getCustomer(); 
            
            AddressEntity addressEntity = new AddressEntity();
            
			String pincode = saveAddressRequest.getPincode(); 
			 
			addressEntity.setPincode(pincode); 
			String city = saveAddressRequest.getCity(); 
			addressEntity.setCity(city); 
			String flatBuildingName = saveAddressRequest.getFlatBuildingName(); 
			addressEntity.setFlatBuilNo(flatBuildingName);
 			String locality = saveAddressRequest.getLocality(); 
 			addressEntity.setLocality(locality); 			
			String stateUuid = saveAddressRequest.getStateUuid(); 
			addressEntity.setActive(1);
			
			String addressUuid = addressService.saveCustomerAddress(stateUuid,customerEntity,addressEntity);
			Map<String,String> keyVsVal = new HashMap<>(); 
			keyVsVal.put("address_uuid", addressUuid); 
			keyVsVal.put("message", "ADDRESS SUCCESSFULLY REGISTERED");

			return new ResponseEntity(keyVsVal, HttpStatus.OK); 
         
        }
        
    	return null;
    	
	}

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AddressEntity>> getAllSavedAddresses(@RequestHeader("authorization") final String authorization
    		) throws  AuthenticationFailedException, AuthorizationFailedException {

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
            
            CustomerEntity customerEntity = customerAuthToken.getCustomer(); 
            List<AddressEntity> addressEntityList = addressService.getAllSavedAddresses(customerEntity);
            return new ResponseEntity<List<AddressEntity>>(addressEntityList,HttpStatus.OK); 
        }
        
        return null;
        
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AddressEntity>> deleteSavedAddresses(@RequestHeader("authorization") final String authorization,
    		@RequestParam("address_id") Integer addressId) throws  AuthenticationFailedException, AuthorizationFailedException,
             AddressNotFoundException {

        String[] splitText = authorization.split("Basic "); 
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        
        CustomerEntity customerEntity = null;
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
            
            customerEntity = customerAuthToken.getCustomer(); 
            
        }
        
        if(addressId == null) {
        	throw new AddressNotFoundException("ANF-005","Address id can not be empty"); 
        }
        
        String addressUuid = addressService.deleteSavedAddress(customerEntity,addressId); 
        Map<String,String> keyVsVal = new HashMap<>();
        keyVsVal.put("uuid", addressUuid);
        keyVsVal.put("message", "ADDRESS DELETED SUCCESSFULLY"); 
        
        return new ResponseEntity(keyVsVal,HttpStatus.OK);  
        
    }
    
    
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StateEntity> > getAllStates() {
            
    	List<StateEntity> allStatesList = addressService.getAllStates(); 
    	return new ResponseEntity<List<StateEntity>>(allStatesList, HttpStatus.OK);  
    	
    }
    
    
}
