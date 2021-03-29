package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemQuantityResponseItem;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getItemByPopularity(@PathVariable("restaurant_id") final String restaurantUuid)
            throws RestaurantNotFoundException {

        //Calls restaurantByUUID method of restaurantService to get the restaurant entity.
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantUuid);

        //Calls getItemsByPopularity method of itemService to get the ItemEntity.
        List<ItemEntity> itemEntities = itemService.getItemsByPopularity(restaurantEntity);

        //Creating the ItemListResponse details as required.
        ItemListResponse itemListResponse = new ItemListResponse();
        itemEntities.forEach(itemEntity -> {
            ItemList itemList = new ItemList()
                    .id(UUID.fromString(itemEntity.getUuid()))
                    .itemName(itemEntity.getItemName())
                    .price(itemEntity.getPrice())
                    .itemType(ItemQuantityResponseItem.TypeEnum.valueOf(itemEntity.getType()));
            itemListResponse.add(itemList);
        });

        return new ResponseEntity<ItemListResponse>(itemListResponse,HttpStatus.OK);
    }
    ItemList.ItemTypeEnum getItemType (String type) {
        if (type.equals("0")) {
            return ItemList.ItemTypeEnum.VEG;
        } else if (type.equals("1")) {
            return ItemList.ItemTypeEnum.NON_VEG;
        } else { // No proper Type, default to NON-VEG
            return ItemList.ItemTypeEnum.VEG;
        }
    }
}
