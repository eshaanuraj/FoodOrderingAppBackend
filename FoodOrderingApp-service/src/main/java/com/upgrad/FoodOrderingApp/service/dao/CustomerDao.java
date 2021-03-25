package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository

public class CustomerDao {
    @PersistenceContext
    EntityManager entityManager;

    /*
     * Create a new Customer
     */
    public CustomerEntity createUser(CustomerEntity customerEntity) {
        entityManager.persist(customerEntity);
        return customerEntity;
    }

    /*
     * Update an existing customer record
     */
    public void updateUser(CustomerEntity customerEntity) {
        entityManager.merge(customerEntity);
    }
    /*
     * Return a record matching the contact number passed
     */
    public CustomerEntity getCustomerByContactNumber(String contactNumber) {
        try {
            return entityManager.createNamedQuery("getCustomerByContactNumber", CustomerEntity.class)
                    .setParameter("contact_number", contactNumber).getSingleResult();
        } catch (NoResultException noResultException) {
            return null;
        }
    }

    /*
     * Create a new Auth Token Entry
     */
    public CustomerAuthTokenEntity createAuthToken(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.persist(customerAuthTokenEntity);
        return customerAuthTokenEntity;
    }

    /*
     * Update Auth Token when a field gets changed
     */
    public void updateAuthToken(final CustomerAuthTokenEntity customerAuthTokenEntity) {
        entityManager.merge(customerAuthTokenEntity);
    }

    public CustomerAuthTokenEntity getAuthtoken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("customerAuthTokenByAccessToken", CustomerAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
