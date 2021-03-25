package com.upgrad.FoodOrderingApp.service.dao;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;

@Repository
public class AddressDao { 

	@PersistenceContext
	private EntityManager entityManager;
	
	
	public AddressEntity saveAddreeEntity(AddressEntity addressEntity) {
		entityManager.persist(addressEntity); 
		return addressEntity;
	}


	public CustomerAddressEntity saveCustomerAddressEntity(CustomerAddressEntity custAddrEntity) { 
		entityManager.persist(custAddrEntity); 
		return custAddrEntity;
	}

	public CustomerAddressEntity getCustomerAddrByAddrId(Integer addressId,Integer customerId) {
		TypedQuery<CustomerAddressEntity> query = entityManager.createNamedQuery("getAddressByAddrId",CustomerAddressEntity.class)
				.setParameter("addr_id", addressId).setParameter("cust_id", customerId); 
		List<CustomerAddressEntity> resultList = query.getResultList(); 
		return resultList.get(0);
	}

	public List<StateEntity> getAllStates() { 
		TypedQuery<StateEntity> query = entityManager.createNamedQuery("getAllStates", StateEntity.class);
		List<StateEntity> allStatesList = query.getResultList(); 
		return allStatesList;
	}


	public List<AddressEntity> getAllSavedAddresses(CustomerEntity customerEntity) {
		TypedQuery<CustomerAddressEntity> query = entityManager.createNamedQuery("getAddressByCustomerId", CustomerAddressEntity.class)
				.setParameter("cust_id", customerEntity.getId()); 
		List<CustomerAddressEntity> allCustAddressEntityList = query.getResultList(); 
		if(allCustAddressEntityList == null || allCustAddressEntityList.size() == 0) {
			return null;
		}
		List<AddressEntity> addressList = new ArrayList<>();
		for(CustomerAddressEntity custAddrEntity : allCustAddressEntityList) {
			addressList.add(custAddrEntity.getAddress());
		}
		return addressList; 
	}
	
	
	public AddressEntity getAddressById(Integer addressId) {
		AddressEntity addressEntity = entityManager.find(AddressEntity.class, addressId); 
		return addressEntity;
	}


	public void deleteSavedAddress(CustomerAddressEntity customerAddressEntity) { 
		entityManager.remove(customerAddressEntity); 
	}
	
    public AddressEntity getAddressByUuid(String uuid){
        try{
            AddressEntity addressEntity = entityManager.createNamedQuery("getAddressByUuid",AddressEntity.class).setParameter("uuid",uuid).getSingleResult();
            return addressEntity;
        }catch (NoResultException nre){ 
            return null;
        }
    }

}
