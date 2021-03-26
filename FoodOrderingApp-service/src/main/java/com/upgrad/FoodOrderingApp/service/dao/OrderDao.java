package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;

@Repository
public class OrderDao {

	@PersistenceContext 
	private EntityManager entityManager;
	 
	
	public List<OrderEntity> getAllOrdersForCustomer(Integer customerId){
		try {
		   List<OrderEntity> resultList = entityManager.createNamedQuery("getOrdersByCustomerId", OrderEntity.class).setParameter("customer_id", customerId).getResultList();
	       return resultList;
		}catch(Exception ex) {
			return null;
		}
	}
	
	
	public void saveOrder(OrderEntity orderEntity) {
		entityManager.persist(orderEntity); 
	}
	
	
}
