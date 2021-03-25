package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    private CommonService commonService;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity update(final String authorization, final String firstname, final String lastname) throws AuthorizationFailedException, UpdateCustomerException {

        CustomerAuthTokenEntity customerAuthTokenEntity;
        CustomerEntity customer;

        // Firstname is mandatory
        if (firstname == null) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        // If there are errors in Authentication, exception would have thrown, its clean if execution continues
        customerAuthTokenEntity = commonService.getCustomerAuthDetails(authorization);

        customer = customerAuthTokenEntity.getCustomer();
        customer.setFirstName(firstname);
        //Update only when lastname is not null
        if (lastname != null) {
            customer.setLastName(lastname);
        }
        customerDao.updateUser(customer);

        return(customer);
    }
}
