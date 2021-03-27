package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;

@Repository
public class CouponDao {

	@PersistenceContext
	private EntityManager entityManager; 

	public CouponEntity getCouponByName(String couponName) {
		try {
			List<CouponEntity> resultList = entityManager.createNamedQuery("getCouponDetailsByName", CouponEntity.class)
					.setParameter("coupon_name","couponName").getResultList();
			return resultList.get(0);
		} catch (Exception ex) {
			return null;
		}
	}
	

	public CouponEntity getCouponByUuid(String uuid) {
		try {
			List<CouponEntity> resultList = entityManager.createNamedQuery("getCouponEntityByUuid", CouponEntity.class)
					.setParameter("uuid","uuid").getResultList();
			return resultList.get(0);
		} catch (Exception ex) {
			return null;
		}
	}
	
	
	
	
}
