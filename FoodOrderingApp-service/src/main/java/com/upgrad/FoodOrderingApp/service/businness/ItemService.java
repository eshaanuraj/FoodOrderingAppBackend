package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryItemDao categoryItemDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private RestaurantItemDao restaurantItemDao;
    @Autowired
    private OrderItemDao orderItemDao;

    @Transactional
    // A Method which takes the itemId as parameter for getItemEntityById
    public ItemEntity getItemEntityById(final Integer itemId){

        return itemDao.getItemById(itemId);
    }

    @Transactional
    // A Method which takes the item uuid as parameter for getItemEntityByUuid
    public ItemEntity getItemEntityByUuid(final String itemUuid) throws ItemNotFoundException{

        ItemEntity itemEntity = itemDao.getItemByUuid(UUID.fromString(itemUuid));
        if (itemEntity == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        } else {
            return itemEntity;
        }
    }

    @Transactional
    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {

        // List to store all items ordered in a restaurant
        List<ItemEntity> itemEntityList = new ArrayList<>();

        // Gets all the orders placed in the restaurant
        for (OrderEntity orderEntity : orderDao.getOrdersByRestaurant(restaurantEntity)) {
            // Gets items from each order placed in the restaurant
            for (OrderItemEntity orderItemEntity : orderItemDao.getItemsByOrder (orderEntity)) {
                itemEntityList.add(orderItemEntity.getItem());
            }
        }

        // Load all the item entities to hashmap
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (ItemEntity itemEntity : itemEntityList) {
            Integer count = map.get(itemEntity.getUuid());
            map.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        }

        // Sorts item entities
        Map<String, Integer> treeMap = new TreeMap<String, Integer>(map);
        List<ItemEntity> sortedItemEntityList = new ArrayList<ItemEntity>();
        for (Map.Entry<String, Integer> entry : treeMap.entrySet()) {
            sortedItemEntityList.add(itemDao.getItemByUuid(UUID.fromString(entry.getKey())));
        }

        // Reverse sort the collections
        Collections.reverse(sortedItemEntityList);

        return sortedItemEntityList;
    }

    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
        return orderItemDao.getItemsByOrder(orderEntity);
    }

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantUuid, String categoryUuid) {

        //Calls getRestaurantByUuid of restaurantDao to get the  RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);

        //Calls getCategoryByUuid of categoryDao to get the  CategoryEntity
        CategoryEntity categoryEntity = categoryDao.getCategoryById(categoryUuid);

        //Calls getItemsByRestaurant of restaurantItemDao to get the  list of RestaurantItemEntity
        List<RestaurantItemEntity> restaurantItemEntities = restaurantItemDao.getItemsByRestaurant(restaurantEntity);

        //Calls getItemsByCategory of categoryItemDao to get the  list of CategoryItemEntity
        List<CategoryItemEntity> categoryItemEntities = categoryItemDao.getAllItemsByCategory(categoryEntity);

        //Creating list of item entity common to the restaurant and category.
        List<ItemEntity> itemEntities = new LinkedList<>();

        restaurantItemEntities.forEach(restaurantItemEntity -> {
            categoryItemEntities.forEach(categoryItemEntity -> {
                if(restaurantItemEntity.getItem().equals(categoryItemEntity.getItem())){
                    itemEntities.add(restaurantItemEntity.getItem());
                }
            });
        });

        return itemEntities;
    }

}

