package com.upgrad.FoodOrderingApp.service.businness;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerDao customerDao;

	public CustomerEntity getCustomerByUUID(String UUID) { 
		return customerDao.getCustomerByUUID(UUID);

	}
	
	
	public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
		CustomerAuthTokenEntity customerAuthToken = customerDao.getAuthtoken(accessToken); 
		final ZonedDateTime now = ZonedDateTime.now();
		if (customerAuthToken == null) {
			throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
		}
		if (customerAuthToken.getLogoutAt() != null) {
			throw new AuthorizationFailedException("ATHR-002",
					"Customer is logged out. Log in again to access this endpoint.");
		}
		if (now.compareTo(customerAuthToken.getExpiresAt()) > 0 ) {
			throw new AuthorizationFailedException("ATHR-003",
					"Your session is expired. Log in again to access this endpoint."); 
		}
		return customerAuthToken.getCustomer();
	}

	public CustomerAuthTokenEntity getAuthToken(String accessToken) {
		CustomerAuthTokenEntity authtoken = customerDao.getAuthtoken(accessToken); 
		return authtoken;
	}

}
