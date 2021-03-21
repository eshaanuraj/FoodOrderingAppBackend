package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class AuthenticationService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)

    /*
     * Login - provide a Auth token if password is correct
     */
    public CustomerAuthTokenEntity signin(final String username, final String password) throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(username);

        // Check If the username(contact number) provided by the user exists . Otherwise throw exception AuthenticationFailedException ATH-001
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());

        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
            CustomerAuthTokenEntity customerAuthTokenEntity = new CustomerAuthTokenEntity();
            customerAuthTokenEntity.setCustomer(customerEntity);
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);

            customerAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            customerAuthTokenEntity.setLoginAt(now);
            customerAuthTokenEntity.setExpiresAt(expiresAt);

            // Create an authorization token and update the token.
            customerDao.createAuthToken(customerAuthTokenEntity);

            return customerAuthTokenEntity;
        }
        // In Correct Password Passed, return an Exception
        else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    /*
     * Logout - logout if a proper Auth token is passed
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity logout(final String authorization) throws AuthorizationFailedException {
        final ZonedDateTime now = ZonedDateTime.now();

        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getAuthtoken(authorization);

        // check if a valid JWT token of an active user is passed
        if (customerAuthTokenEntity != null) {

            // Check if the user has already logged out
            if (customerAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint");
            }

            // Check if token has expired
            final ZonedDateTime expiry = customerAuthTokenEntity.getExpiresAt();
            if (now.compareTo(expiry) > 0) {
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint");
            }

            // Update the logout time and update the record in the database
            customerAuthTokenEntity.setLogoutAt(now);
            customerDao.updateAuthToken(customerAuthTokenEntity);

            return (customerAuthTokenEntity);

        } else {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        }
    }
}
