package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Repository
public class PaymentDao {
    @PersistenceContext
    private EntityManager entityManager;

    // Get all Payment methods, throws NoResultException
    public List<PaymentEntity> getAllPaymentMethods(){

        try {
            return entityManager.createNamedQuery("getAllPaymentMethods", PaymentEntity.class).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

	public PaymentEntity getPaymentByUuid(UUID uuid) { 
		// TODO Auto-generated method stub

        try {
            return entityManager.createNamedQuery("getPaymentbyUuid", PaymentEntity.class).setParameter("uuid",uuid.toString()).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
        
	}
	
}
