package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    RestaurantDao restaurantDao; //Handles all data related to the RestaurantEntity

    /* This method will get restaurants By Rating and returns list of RestaurantEntity
      Throws exception with error code and error message.
      */
    public List<RestaurantEntity> getRestaurantsByRating(){

        //Calls restaurantsByRating of restaurantDao to get list of RestaurantEntity
        List<RestaurantEntity> restaurantEntities = restaurantDao.getRestaurantsByRating();
        return restaurantEntities;
    }

    /* This method will get restaurants By Name and returns list of RestaurantEntity. This method takes Restaurant name as parameter.
    In case of an error, throws exception with error code and error message.
    */
    public List<RestaurantEntity> getRestaurantsByName(String restaurantName)throws RestaurantNotFoundException{
        //Check if restaurant name is null or empty to throw exception.
        if(restaurantName == null || restaurantName ==""){
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");
        }

        //Method to call getRestaurantsByName Method of  restaurantDao to get list of RestaurantEntities
        List<RestaurantEntity> restaurantEntities = restaurantDao.getRestaurantsByName(restaurantName);
        return restaurantEntities;
    }

    /* This method will get restaurant By UUID and returns RestaurantEntity. Method takes restaurantUuid as parameter.
     In case of error, throws exception with error code and error message "Restaurant id field should not be empty".
     */
    public RestaurantEntity restaurantByUUID(String restaurantUuid)throws RestaurantNotFoundException{
        if(restaurantUuid == null||restaurantUuid == ""){ //Checking for restaurantUuid to be null or empty to throw exception.
            throw new RestaurantNotFoundException("RNF-002","Restaurant id field should not be empty");
        }

        //Method to get getRestaurantByUuid of restaurantDao to get the  RestaurantEntity
        RestaurantEntity restaurantEntity = restaurantDao.getRestaurantByUuid(restaurantUuid);
        //Check if restaurantEntity is null or empty to throw exception, "No restaurant by this id".
        if (restaurantEntity == null){
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }

        return restaurantEntity;


    }


}
