package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.UpdateBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
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
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();

        // Except for Lastname all fields should be non-null
        if ((signupCustomerRequest.getFirstName() == null) || (signupCustomerRequest.getFirstName().equals("")) ||
                (signupCustomerRequest.getEmailAddress() == null) || (signupCustomerRequest.getEmailAddress().equals("")) ||
                (signupCustomerRequest.getPassword() == null ) || (signupCustomerRequest.getPassword().equals("")) ||
                (signupCustomerRequest.getContactNumber() == null) || signupCustomerRequest.getContactNumber().equals("")) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
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
        byte[] decoder = Base64.getDecoder().decode(splitText[1]);
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

        String[] splitText = authorization.split("Bearer ");
        String accessToken = new String(splitText[1]);

        CustomerAuthTokenEntity logout = authenticationService.logout(accessToken);

        LogoutResponse logoutResponse = new LogoutResponse().id(logout.getCustomer().getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);

    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/customer", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> update(@RequestHeader("authorization") final String authorization, @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerEntity customer;

        String[] splitText = authorization.split("Bearer ");
        String accessToken = new String(splitText[1]);

        if ((updateCustomerRequest.getFirstName() == null) || (updateCustomerRequest.getFirstName().equals(""))){
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        customer = updateBusinessService.getCustomer(accessToken);

        customer.setFirstName(updateCustomerRequest.getFirstName());
        if ((updateCustomerRequest.getLastName() != null) && (updateCustomerRequest.getLastName().equals("")==false)) {
            customer.setLastName(updateCustomerRequest.getLastName());
        }
        customer = updateBusinessService.updateCustomer(customer);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse().id(customer.getUuid()).status("CUSTOMER DETAILS UPDATED SUCCESSFULLY").
                firstName(customer.getFirstName()).lastName(customer.getLastName());

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestHeader("authorization") final String authorization, @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest) throws AuthorizationFailedException, UpdateCustomerException {
        CustomerEntity customer;

        String[] splitText = authorization.split("Bearer ");
        String accessToken = new String(splitText[1]);

        if ((updatePasswordRequest.getOldPassword() == null) || (updatePasswordRequest.getNewPassword() == null) ||
                (updatePasswordRequest.getOldPassword().equals("")) ||
                (updatePasswordRequest.getNewPassword().equals(""))) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        customer = updateBusinessService.getCustomer(accessToken);
        customer = updateBusinessService.updateCustomerPassword(updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), customer);

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse().id(customer.getUuid()).status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}