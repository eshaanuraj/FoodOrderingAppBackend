package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;

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

	public List<OrderEntity> getOrdersByCustomerUUID(String customerUUId){
		try {
		   List<OrderEntity> resultList = entityManager.createNamedQuery("getOrdersByCustomerUUId", OrderEntity.class).setParameter("customerUUId", customerUUId).getResultList();
	       return resultList;
		}catch(Exception ex) {
			return null;
		}
	}

	
	
	public OrderEntity saveOrder(OrderEntity orderEntity) {
		entityManager.persist(orderEntity); 
		return orderEntity;
	}


	public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
		entityManager.persist(orderItemEntity); 
		return orderItemEntity;
	}
	
	
}
