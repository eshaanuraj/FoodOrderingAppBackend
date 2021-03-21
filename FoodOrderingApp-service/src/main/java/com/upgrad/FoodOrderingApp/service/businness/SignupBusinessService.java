package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignupBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {

        final CustomerEntity customerContactNumber = customerDao.getCustomerByContactNumber(customerEntity.getContactNumber());

        // Except for Lastname all fields should be non-null
        if ((customerEntity.getFirstName() == null) || (customerEntity.getEmail() == null) ||
                (customerEntity.getPassword() == null ) || (customerEntity.getContactNumber() == null)) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        // Throw an exception if a record with same contact number already exists
        if (null != customerContactNumber) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }

        // check if the passed email is in proper format
        if (isEmailValid(customerEntity.getEmail()) == false) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        // check if the passed contact number  is in proper format
        if (isPhoneNumberValid(customerEntity.getContactNumber()) == false) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }

        // check if the password passed is in proper format
        if (isPasswordValid(customerEntity.getPassword()) == false) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        // Encrypt the password and store the salt
        String[] encrypt = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encrypt[0]);
        customerEntity.setPassword(encrypt[1]);
        return customerDao.createUser(customerEntity);
    }

    static boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern patCheck = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return patCheck.matcher(email).matches();
    }

    static boolean isPhoneNumberValid(String number)
    {
        // 1) Contains digits 0-9
        // 2) Contains 10 digits
        Pattern patCheck = Pattern.compile("[0-9]{10}");
        if (number == null)
            return false;
        return patCheck.matcher(number).matches();
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
