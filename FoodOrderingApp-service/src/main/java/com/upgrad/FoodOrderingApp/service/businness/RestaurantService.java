package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
public class RestaurantService {
    //Handles all data related to the RestaurantEntity
    @Autowired
    RestaurantDao restaurantDao;

    @Autowired
    CommonService commonService;
    // This Method is to get getAllRestaurants endpoint
    public List<RestaurantEntity> getAllRestaurants() {
        return restaurantDao.getAllRestaurants();
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

    // Method to get the restaurants by the categoryUUID as input parameter
    public List<RestaurantCategoryEntity> getRestaurantByCategoryId(final Integer categoryID) {
        return restaurantDao.getRestaurantByCategoryId(categoryID);
    }

    @Transactional
    public RestaurantEntity updateRestaurantDetails (final Double customerRating, final String restaurant_id, final String authorizationToken)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        final ZonedDateTime now = ZonedDateTime.now();

        // Validates the customer using the authorizationToken
        commonService.getCustomerAuthDetails(authorizationToken);

        // Throw exception if path variable(restaurant_id) is empty
        if(restaurant_id == null || restaurant_id.isEmpty() || restaurant_id.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        //get the restaurant Details using the restaurantUuid
        RestaurantEntity restaurantEntity =  restaurantDao.getRestaurantByUuid(restaurant_id);

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Throw exception if path variable(restaurant_id) is empty
        if(customerRating == null || customerRating.isNaN() || customerRating < 1 || customerRating > 5 ){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        // update rating and add it to the restaurantEntity
        BigDecimal oldRating = (restaurantEntity.getCustomerRating().multiply(new BigDecimal(restaurantEntity.getNumCustomersRated())));
        BigDecimal newRating = (oldRating.add(new BigDecimal(customerRating))).divide(new BigDecimal(restaurantEntity.getNumCustomersRated() + 1));
        restaurantEntity.setCustomerRating(newRating);
        restaurantEntity.setNumCustomersRated(restaurantEntity.getNumCustomersRated() + 1);

        //called restaurantDao to merge the content and update in the database
        restaurantDao.updateRestaurantDetails(restaurantEntity);
        return restaurantEntity;
    }
}
