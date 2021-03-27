package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

@Service
public class OrderService {

	
	@Autowired
	private CouponDao couponDao;
	
	
	@Autowired
	private OrderDao orderDao;
	
	
	@Autowired
	private ItemDao itemDao;
	
	
	public CouponEntity getCouponByName(String couponName) throws CouponNotFoundException { 

		if(StringUtils.isEmpty(couponName)) {
			throw new CouponNotFoundException("CPF-002","Coupon name field should not be empty");
		}
	
		CouponEntity couponByName = couponDao.getCouponByName(couponName);
		if(couponByName == null) {
			throw new CouponNotFoundException("CPF-001","No coupon by this name");
		}
		return couponByName;
	}
	
	
	public CouponEntity getCouponByUuid(String uuid) {
		CouponEntity couponByUuid = couponDao.getCouponByUuid(uuid);
		return couponByUuid;
	}
	
	public List<OrderEntity> getAllOrdersForCustomer(CustomerEntity customer) {
		
		List<OrderEntity> allOrdersForCustomer = orderDao.getAllOrdersForCustomer(customer.getId());
		return allOrdersForCustomer; 
		
	}


	public OrderEntity saveOrder(OrderEntity orderEntity) {
		OrderEntity savedOrder = orderDao.saveOrder(orderEntity);  
		return savedOrder;
	}


	public ItemEntity getItemByUuid(@Valid UUID uuid) { 
		ItemEntity itemByUuid = itemDao.getItemByUuid(uuid); 
		return itemByUuid;
	}
	

}
