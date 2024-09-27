package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private CategoryItemDao categoryItemDao;

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private RestaurantCategoryDao restaurantCategoryDao;

    // Service to get all category Records
    @Transactional
    public List<CategoryEntity> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    // Service to get a category based on the UUID passed
    public CategoryEntity getCategoryById(String uuid) throws CategoryNotFoundException{

        CategoryEntity categoryEntity;
        List<ItemEntity> itemEntityList = new ArrayList<>();

        // Check if passed UUID is NULL
        if (uuid == null) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        // Check if Category Exists in the Table
        categoryEntity = categoryDao.getCategoryById(uuid);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        itemEntityList = getItemsByCategory(categoryEntity);

        categoryEntity.setItems(itemEntityList);

        return categoryEntity;
    }

    public List<ItemEntity> getItemsByCategory(CategoryEntity category) {
        List<CategoryItemEntity> categoryItemEntities = new ArrayList<>();
        List <ItemEntity> itemEntityList = new ArrayList<>();
        ItemEntity item;

        // Get the list of all item ID's for a given category
        categoryItemEntities = categoryItemDao.getAllItemsByCategory(category);

        // Iterate thru each of them to get the item details
        for (CategoryItemEntity categoryItemEntity : categoryItemEntities) {
            itemEntityList.add(categoryItemEntity.getItem());
        }
        return itemEntityList;
    }

    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUuid){

        //Calls getRestaurantByUuid of restaurantDao to get RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        //Calls getCategoriesByRestaurant of restaurantCategoryDao to get list of RestaurantCategoryEntity
        List<RestaurantCategoryEntity> restaurantCategoryEntities = restaurantCategoryDao.getCategoriesByRestaurant(restaurantEntity);

        //Creating the list of the Category entity to be returned.
        List<CategoryEntity> categoryEntities = new LinkedList<>();
        restaurantCategoryEntities.forEach(restaurantCategoryEntity -> {
            categoryEntities.add(restaurantCategoryEntity.getCategory());
        });
        return categoryEntities;
    }
}
