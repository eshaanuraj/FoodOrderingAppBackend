package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
