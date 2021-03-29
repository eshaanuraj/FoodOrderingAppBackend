package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class CommonService {

    @Autowired
    private CustomerDao customerDao;

    public CustomerAuthTokenEntity getCustomerAuthDetails(final String authorization) throws AuthorizationFailedException {

        CustomerAuthTokenEntity customerAuthTokenEntity = customerDao.getAuthtoken(authorization);
        final ZonedDateTime now = ZonedDateTime.now();

        // check if a valid JWT token of an active user is passed
        if (customerAuthTokenEntity != null) {

            // Check if the user has already logged out
            if (customerAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint");
            }

            // Check if token has expired
            final ZonedDateTime expiry = customerAuthTokenEntity.getExpiresAt();
            if (now.isAfter(expiry)) {
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint");
            }
        }
        // In Correct Password Passed, return an Exception
        else {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        }
        return (customerAuthTokenEntity);
    }
}
