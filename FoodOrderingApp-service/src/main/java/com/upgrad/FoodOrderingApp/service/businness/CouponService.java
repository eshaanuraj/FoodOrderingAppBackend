package com.upgrad.FoodOrderingApp.service.businness;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;

@Service
public class CouponService {


	@Autowired
	private CouponDao couponDao;
	
	
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
	
	

}
