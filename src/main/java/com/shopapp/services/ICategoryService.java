package com.shopapp.services;

import com.shopapp.dtos.CategoryDTO;
import com.shopapp.models.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory (CategoryDTO category);

    Category getCategoryById (Long id);

    List<Category> getAllCategories ();

    Category updateCategory (Long categoryId, CategoryDTO category);
    void deleteCategory (Long id);
}
