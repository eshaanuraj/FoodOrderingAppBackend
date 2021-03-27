package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.businness.StateService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    @Autowired
    AddressService addressService;

    @Autowired
    StateService stateService;

    @Autowired
    CategoryService categoryService ;

    /** Implementation of getAllRestaurants endpoint.
     * This method expose end point /restaurants
     * @return List<RestaurantEntity>
     *
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        // Getting the list of all restaurants with help of restaurant business service
        final List<RestaurantEntity> allRestaurants = restaurantService.getAllRestaurants();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        // Add individual restaurants to RestaurantList
        List<RestaurantList> details = new ArrayList<RestaurantList>();
        for (RestaurantEntity re: allRestaurants) {
            RestaurantList detail = new RestaurantList();
            detail.setId(UUID.fromString(re.getUuid()));
            detail.setRestaurantName(re.getRestaurantName());
            detail.setPhotoURL(re.getPhotoUrl());
            detail.setCustomerRating(re.getCustomerRating());
            detail.setAveragePrice(re.getAvgPriceForTwo());
            detail.setNumberCustomersRated(re.getNumCustomersRated());

            // Get address of restaurant from address entity
            AddressEntity addressEntity = addressService.getAddressById(re.getAddress().getId());
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Get state for current address from state entity
            StateEntity stateEntity = stateService.getStateById(addressEntity.getState().getId());
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // Set address with state into restaurant obj
            detail.setAddress(responseAddress);

            // Get categories and setting name values only
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :re.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sort Category list by Name of the category
            Collections.sort(categoryLists);

            // Join List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            // Add category detail to details(RestaurantList)
            restaurantListResponse.addRestaurantsItem(detail);
        }

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /** Implementation of getRestaurantsByName endpoint.
     * This method exposes the endpoint /restaurant/name/{restaurant_name}
     * @param restaurant_name
     * @return List of all restaurants matched with given restaurant name
     * @throws RestaurantNotFoundException - when restaurant name field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable String restaurant_name)
            throws RestaurantNotFoundException {

        // Throw exception if variable(restaurant_name) is empty
        if(restaurant_name == null || restaurant_name.isEmpty() || restaurant_name.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        // Get the list of all restaurants from restaurant service based on input restaurant name
        List<RestaurantEntity> allRestaurants = restaurantService.getRestaurantsByName(restaurant_name);

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        // Add the list of restaurants to RestaurantList
        List<RestaurantList> details = new ArrayList<RestaurantList>();
        for (RestaurantEntity n: allRestaurants) {
            RestaurantList detail = new RestaurantList();
            detail.setId(UUID.fromString(n.getUuid()));
            detail.setRestaurantName(n.getRestaurantName());
            detail.setPhotoURL(n.getPhotoUrl());
            detail.setCustomerRating(n.getCustomerRating());
            detail.setAveragePrice(n.getAvgPriceForTwo());
            detail.setNumberCustomersRated(n.getNumCustomersRated());

            // Get the address of restaurant from address entity
            AddressEntity addressEntity = addressService.getAddressById(n.getAddress().getId());
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Set the  state for current address from state entity
            StateEntity stateEntity = stateService.getStateById(addressEntity.getState().getId());
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // set address with state into restaurant obj
            detail.setAddress(responseAddress);

            // Loop through the  categories and set name values only
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :n.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sort the category list by Category name
            Collections.sort(categoryLists);

            // Joining the List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            restaurantListResponse.addRestaurantsItem(detail);

        }

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /** Implementation of getRestaurantsByCategoryId endpoint
     * This method exposes the endpoint /restaurant/category/{category_id}
     * @param category_id
     * @return List of all restaurants with given category id
     * @throws CategoryNotFoundException - When Given category id  field is empty
     */
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity getRestaurantByCategoryId(@PathVariable String category_id) throws CategoryNotFoundException {

        // Throw exception if path variable(category_id) is empty
        if(category_id == null || category_id.isEmpty() || category_id.equalsIgnoreCase("\"\"")){
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        // Get category which matched with given category_id
        CategoryEntity matchedCategory = categoryService.getCategoryById(category_id);

        // Throw exception if given category_id not matched with any category in DB
        if(matchedCategory == null){
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }

        // If given category_id match with any category in DB, then get all restaurants having matched category
        final List<RestaurantCategoryEntity> allRestaurantCategories = restaurantService.getRestaurantByCategoryId(matchedCategory.getId());

        // Add the list of restaurants to RestaurantList
        List<RestaurantList> details = new ArrayList<RestaurantList>();
        for (RestaurantCategoryEntity restaurantCategoryEntity:allRestaurantCategories) {
            RestaurantEntity n = restaurantCategoryEntity.getRestaurant();
            RestaurantList detail = new RestaurantList();
            detail.setId(UUID.fromString(n.getUuid()));
            detail.setRestaurantName(n.getRestaurantName());
            detail.setPhotoURL(n.getPhotoUrl());
            detail.setCustomerRating(n.getCustomerRating());
            detail.setAveragePrice(n.getAvgPriceForTwo());
            detail.setNumberCustomersRated(n.getNumCustomersRated());

            // Get address of restaurant from address entity
            AddressEntity addressEntity = addressService.getAddressById(n.getAddress().getId());
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress();

            responseAddress.setId(UUID.fromString(addressEntity.getUuid()));
            responseAddress.setFlatBuildingName(addressEntity.getFlatBuilNo());
            responseAddress.setLocality(addressEntity.getLocality());
            responseAddress.setCity(addressEntity.getCity());
            responseAddress.setPincode(addressEntity.getPincode());

            // Get state for current address from state entity
            StateEntity stateEntity = stateService.getStateById(addressEntity.getState().getId());
            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState();

            responseAddressState.setId(UUID.fromString(stateEntity.getUuid()));
            responseAddressState.setStateName(stateEntity.getStateName());
            responseAddress.setState(responseAddressState);

            // Set address with state into restaurant obj
            detail.setAddress(responseAddress);

            // Loop through the categories to add category names to the list
            List<String> categoryLists = new ArrayList();
            for (CategoryEntity categoryEntity :n.getCategoryEntities()) {
                categoryLists.add(categoryEntity.getCategoryName());
            }

            // Sort category names in alphabetical order
            Collections.sort(categoryLists);

            // Join List items as string with comma(,)
            detail.setCategories(String.join(",", categoryLists));

            // Add category detail to details(RestaurantList)
            details.add(detail);
        }

        // return response entity with RestaurantLists(details) and Http status
        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /** Implements updateRestaurantDetails Endpoint
     * This method exposes the endpoint /restaurant/{restaurant_id}
     * @param authorization, customerRating, restaurant_id
     * @return Restaurant uuid of the rating updated restaurant
     * @throws RestaurantNotFoundException - When given restaurant id field is empty
     *         AuthorizationFailedException - When customer is not logged in or logged out or login expired
     *         InvalidRatingException - When the Rating value provided is invalid
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(@RequestHeader("authorization") final String authorization, @RequestParam Double customerRating, @PathVariable String restaurant_id )
            throws AuthorizationFailedException, InvalidRatingException, RestaurantNotFoundException {

        // Get the authorization Token
        String[] bearerToken = authorization.split("Bearer ");

        // Access the RestaurantBusinessService to update the customer
        RestaurantEntity restaurantEntity = restaurantService.updateRestaurantDetails(customerRating, restaurant_id, bearerToken[1]);

        // Retrieve the status to return the details to the updateResponse
        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantEntity.getUuid())).status("RESTAURANT RATING UPDATED SUCCESSFULLY");

        // Return the RestaurantUpdatedResponse with OK http status
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }
}
