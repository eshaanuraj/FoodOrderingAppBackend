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

    @Autowired
    private CommonService commonService;

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

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());

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
        CustomerAuthTokenEntity customerAuthTokenEntity;

        // If there are errors in Authentication, exception would have thrown, its clean if execution continues
        customerAuthTokenEntity = commonService.getCustomerAuthDetails(authorization);

        // Update the logout time and update the record in the database
        customerAuthTokenEntity.setLogoutAt(now);
        customerDao.updateAuthToken(customerAuthTokenEntity);

        return (customerAuthTokenEntity);
    }
}
