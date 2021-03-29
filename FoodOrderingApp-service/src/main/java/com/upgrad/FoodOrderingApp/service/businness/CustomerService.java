package com.upgrad.FoodOrderingApp.service.businness;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerDao customerDao;

	public CustomerEntity getCustomerByUUID(String UUID) { 
		return customerDao.getCustomerByUUID(UUID);

	}
	
	
	public CustomerEntity getCustomer(String accessToken) {
		CustomerAuthTokenEntity authtoken = customerDao.getAuthtoken(accessToken); 
		return authtoken.getCustomer();
	}

	public CustomerAuthTokenEntity getAuthToken(String accessToken) {
		CustomerAuthTokenEntity authtoken = customerDao.getAuthtoken(accessToken); 
		return authtoken;
	}

}
