package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
public class UpdateBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    private CommonService commonService;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String authorization) throws AuthorizationFailedException, UpdateCustomerException {

        return commonService.getCustomerAuthDetails(authorization).getCustomer();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {

        // Firstname is mandatory
        if (customer.getFirstName() == null) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        customerDao.updateUser(customer);

        return(customer);
    }

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

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldpass, final String newpass, CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {

        // Old password and new password fields are mandatory
        if ((oldpass == null) || (newpass == null)) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        //Check if old password passed is same as what is there in the DB
        final String encryptedPassword = passwordCryptographyProvider.encrypt(oldpass, customer.getSalt());
        if (encryptedPassword.equals(customer.getPassword()) == false) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        //Check if the new password adheres to the password strength requirements
        if (isPasswordValid(newpass) == false) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        // Encrypt the new password and the salt and update DB
        String[] encrypt = passwordCryptographyProvider.encrypt(newpass);
        customer.setSalt(encrypt[0]);
        customer.setPassword(encrypt[1]);

        customerDao.updateUser(customer);

        return(customer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updatePassword(final String authorization, final String oldpass, final String newpass) throws AuthorizationFailedException, UpdateCustomerException {

        CustomerAuthTokenEntity customerAuthTokenEntity;
        CustomerEntity customer;

        // Old password and new password fields are mandatory
        if ((oldpass == null) || (newpass == null)) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }

        // If there are errors in Authentication, exception would have thrown, its clean if execution continues
        customerAuthTokenEntity = commonService.getCustomerAuthDetails(authorization);
        customer = customerAuthTokenEntity.getCustomer();

        //Check if old password passed is same as what is there in the DB
        final String encryptedPassword = passwordCryptographyProvider.encrypt(oldpass, customer.getSalt());
        if (encryptedPassword.equals(customer.getPassword()) == false) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }
        //Check if the new password adheres to the password strength requirements
        if (isPasswordValid(newpass) == false) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        // Encrypt the new password and the salt and update DB
        String[] encrypt = passwordCryptographyProvider.encrypt(newpass);
        customer.setSalt(encrypt[0]);
        customer.setPassword(encrypt[1]);

        customerDao.updateUser(customer);

        return(customer);
    }

    static boolean isPasswordValid(String password)
    {
        // 1) Contains atleast 1 digits 0-9
        // 2) Contains atleast 1 uppercase and lowercase
        // 3) Contains atleast 1 special character from "@#$%^&+="
        // 4) Contains minimium 8 characters
        String passwordregex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        Pattern patCheck = Pattern.compile(passwordregex);
        if (password == null)
            return false;
        return patCheck.matcher(password).matches();
    }
}
