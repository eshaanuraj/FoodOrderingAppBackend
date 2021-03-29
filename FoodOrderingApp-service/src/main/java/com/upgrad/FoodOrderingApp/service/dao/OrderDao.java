package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;

@Repository 
public class OrderDao {

	@PersistenceContext 
	private EntityManager entityManager;
	 
	
	public List<OrderEntity> getAllOrdersForCustomer(CustomerEntity customer){
		try {
		   List<OrderEntity> resultList = entityManager.createNamedQuery("getOrdersByCustomerId", OrderEntity.class).setParameter("customer", customer).getResultList();
	       return resultList;
		}catch(Exception ex) {
			return null;
		}
	}

	public List<OrderEntity> getOrdersByCustomerUUID(CustomerEntity customer){
		try {
		   List<OrderEntity> resultList = entityManager.createNamedQuery("getOrdersByCustomerUUId", OrderEntity.class).setParameter("customer", customer).getResultList();
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
	public List<OrderEntity> getOrdersByRestaurant(final RestaurantEntity restaurantEntity) {
		try {
			return entityManager.createNamedQuery("getOrdersByRestaurant", OrderEntity.class)
					.setParameter("restaurant", restaurantEntity).getResultList();
		} catch (NoResultException nre) {
			return null;
		}
	}
	
}
