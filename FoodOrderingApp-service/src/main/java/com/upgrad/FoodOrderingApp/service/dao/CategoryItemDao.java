package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryItemDao {
    @PersistenceContext
    EntityManager entityManager;

    // Get all items by category
    public List<CategoryItemEntity> getAllItemsByCategory(CategoryEntity category){

        try {
            return entityManager.createNamedQuery("getAllItemsByCategory", CategoryItemEntity.class).setParameter("category", category).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
}
