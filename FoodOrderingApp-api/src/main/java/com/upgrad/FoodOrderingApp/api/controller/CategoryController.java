package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")

public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse>getAllCategories() {

        List<CategoryEntity> categoryEntityList = new ArrayList<>();
        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        // Get all records from DB
        categoryEntityList.addAll(categoryService.getAllCategories());

        // Add each category Entry to the list
        for (CategoryEntity categoryEntity : categoryEntityList) {
            CategoryListResponse categoryListResponse = new CategoryListResponse();
            categoryListResponse.setId(UUID.fromString(categoryEntity.getUuid()));
            categoryListResponse.setCategoryName(categoryEntity.getCategoryName());
            categoriesListResponse.addCategoriesItem(categoryListResponse);
        }
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/category/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse>getCategoryDetails(@PathVariable("id") final String id) throws CategoryNotFoundException {

        CategoryEntity category;
        List<ItemEntity> itemEntityList = new ArrayList<>();
        List <ItemList> itemLists = new ArrayList<>();
        CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse();

        // Get the Category corresponding to the UUID
        category = categoryService.getCategoryById(id);

        // Get the list of items corresponding to the category
        itemEntityList = category.getItems();

        categoryDetailsResponse.setCategoryName(category.getCategoryName());
        categoryDetailsResponse.setId(UUID.fromString(category.getUuid()));

        for (ItemEntity itemEntity : itemEntityList) {
            ItemList item = new ItemList();

            item.setId(UUID.fromString(itemEntity.getUuid()));
            item.setItemName(itemEntity.getItemName());
            item.setPrice(itemEntity.getPrice());
            item.setItemType(getItemType(itemEntity.getType()));
            itemLists.add(item);
        }
        categoryDetailsResponse.setItemList(itemLists);

        return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
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
