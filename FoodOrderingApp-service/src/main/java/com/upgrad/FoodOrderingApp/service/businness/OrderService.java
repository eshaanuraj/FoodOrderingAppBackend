package com.upgrad.FoodOrderingApp.service.businness;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

@Service
public class OrderService {

	
	@Autowired
	private CouponDao couponDao;
	
	
	@Autowired
	private OrderDao orderDao;
	
	
	public CouponEntity getCouponByName(String couponName) throws CouponNotFoundException { 

		if(couponName == null || couponName.strip() == null) {
			throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
		}
	
		CouponEntity couponByName = couponDao.getCouponByName(couponName);
		if(couponByName == null) {
			throw new CouponNotFoundException("CPF-001","No coupon by this name");
		}
		return couponByName;
	}
	
	
	public List<OrderEntity> getAllOrdersForCustomer(CustomerEntity customer) {
		
		List<OrderEntity> allOrdersForCustomer = orderDao.getAllOrdersForCustomer(customer.getId());
		return allOrdersForCustomer; 
		
	}


	public void saveOrder(OrderEntity orderEntity) {
		// TODO Auto-generated method stub 
		orderDao.saveOrder(orderEntity); 
		
	}
	

}
