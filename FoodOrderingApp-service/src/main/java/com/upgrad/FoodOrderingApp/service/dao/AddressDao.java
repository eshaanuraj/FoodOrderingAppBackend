package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import org.springframework.stereotype.Repository;


import javax.persistence.*;

@Repository
public class AddressDao {

    //To get address by UUID if no results null is returned.
    @PersistenceContext
    private EntityManager entityManager;

    public AddressEntity getAddressByUuid(String uuid){
        try{
            AddressEntity addressEntity = entityManager.createNamedQuery("getAddressByUuid",AddressEntity.class).setParameter("uuid",uuid).getSingleResult();
            return addressEntity;
        }catch (NoResultException nre){
            return null;
        }
    }
}
