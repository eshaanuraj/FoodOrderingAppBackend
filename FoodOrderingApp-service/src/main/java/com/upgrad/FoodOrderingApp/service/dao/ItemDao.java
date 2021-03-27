package com.upgrad.FoodOrderingApp.service.dao;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;


@Repository
public class ItemDao {

	@PersistenceContext
	private EntityManager entityManager;

	public ItemEntity getItemByUuid(UUID uuid) {
		try {
			List<ItemEntity> resultList = entityManager.createNamedQuery("getItemByUuid", ItemEntity.class)
					.setParameter("uuid", uuid).getResultList();
			return resultList.get(0);
		} catch (Exception ex) {
			return null;
		}

	}
}
