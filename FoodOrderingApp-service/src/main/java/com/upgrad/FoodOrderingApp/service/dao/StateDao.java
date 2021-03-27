package com.upgrad.FoodOrderingApp.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;

import javax.persistence.NoResultException;

@Repository
public class StateDao {

	@PersistenceContext
	EntityManager entityManager;

	public StateEntity getStateEntityById(String id) {
		return null;
	}

	public StateEntity getStateEntityByUuid(String stateUuid) {
		
		try {
			
			StateEntity stateEntity = entityManager.createNamedQuery("getStateByUuid", StateEntity.class)
					.getSingleResult();
			return stateEntity;
			
		} catch (Exception ex) {
			return null;
		}
		
	}



    public StateEntity getStateById(final Long stateId) {
        try {
            return entityManager.createNamedQuery("getStateById", StateEntity.class).setParameter("id", stateId)
                    .getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }
    
}

