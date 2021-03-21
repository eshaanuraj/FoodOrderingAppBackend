package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {

        final CustomerEntity customerContactNumber = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        // Throw an exception if a record with same contact number already exists
        if (null != customerContactNumber) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        // Encrypt the password and store the salt
        String[] encrypt = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encrypt[0]);
        customerEntity.setPassword(encrypt[1]);
        return customerDao.createUser(customerEntity);
    }
}
