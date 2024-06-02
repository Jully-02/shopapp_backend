package com.shopapp.controllers;

import com.shopapp.components.LocalizationUtils;
import com.shopapp.dtos.CategoryDTO;
import com.shopapp.models.Category;
import com.shopapp.responses.UpdateCategoryResponse;
import com.shopapp.services.CategoryService;
import com.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("${api.prefix}/categories")
// Dependency Injection
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    // What if the input parameter is an object? => Data Transfer Object (DTO) = Request Object
    public ResponseEntity<?> createCategory (@Valid @RequestBody CategoryDTO categoryDTO, BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            categoryService.createCategory(categoryDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_FAILED));
        }
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.INSERT_CATEGORY_SUCCESSFULLY));
    }

    // Show all categories
    @GetMapping("") // http://localhost:8088/api/v1/categories?page=1&limit=16
    public ResponseEntity<List<Category>> getAllCategories (
            @RequestParam("page")   int page,
            @RequestParam("limit")  int limit
    ) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory (
            @PathVariable Long id,
            @RequestBody CategoryDTO categoryDTO
    ) {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable("id") Long categoryId) {
        Category exisitingCategory = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(exisitingCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory (@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(localizationUtils.getLocalizedMessage(MessageKeys.DELETE_CATEGORY_SUCCESSFULLY, id));
    }
}
