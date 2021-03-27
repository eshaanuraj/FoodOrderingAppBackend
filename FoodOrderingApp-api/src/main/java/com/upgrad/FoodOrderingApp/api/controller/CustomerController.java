package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UpdateBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    SignupBusinessService signupBusinessService;

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UpdateBusinessService updateBusinessService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt("salt");
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        CustomerEntity signup = signupBusinessService.signup(customerEntity);
        SignupCustomerResponse signupCustomerResponse = new SignupCustomerResponse().id(signup.getUuid()).status(" CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(signupCustomerResponse, HttpStatus.CREATED);

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        String[] splitText = authorization.split("Basic ");
        LoginResponse loginResponse = null;
        HttpHeaders headers = new HttpHeaders();
        byte[] decoder = Base64.getDecoder().decode(splitText[0]);
        String decodedText = new String(decoder);
        String[] decodedTextArray = decodedText.split(":");
        if (decodedTextArray.length == 2) {
            CustomerAuthTokenEntity customerAuthToken = authenticationService.signin(decodedTextArray[0], decodedTextArray[1]);

            CustomerEntity customer = customerAuthToken.getCustomer();

            loginResponse = new LoginResponse().id(customer.getUuid())
                    .message("LOGGED IN SUCCESSFULLY").firstName(customer.getFirstName())
                    .lastName(customer.getLastName()).emailAddress(customer.getEmail())
                    .contactNumber(customer.getContactNumber());

            // Set Access-Control-Expose-Headers in the response header
            List<String> header = new ArrayList<>();
            header.add("access-token");
            headers.setAccessControlExposeHeaders(header);

            headers.add("access-token", customerAuthToken.getAccessToken());
            return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);
        } else {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        CustomerAuthTokenEntity logout = authenticationService.logout(authorization);

        LogoutResponse logoutResponse = new LogoutResponse().id(logout.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") final String authorization, final UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerEntity customer;

        customer = updateBusinessService.update(authorization, updateCustomerRequest.getFirstName(), updateCustomerRequest.getLastName());

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(customer.getUuid()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY").
                firstName(customer.getFirstName()).lastName(customer.getLastName());

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader("authorization") final String authorization, final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerEntity customer;

        customer = updateBusinessService.updatePassword(authorization, updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword());

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}