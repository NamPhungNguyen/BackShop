package com.appshop.back_shop.controller;

import com.appshop.back_shop.domain.Category;
import com.appshop.back_shop.dto.request.categories.CategoryRequest;
import com.appshop.back_shop.dto.response.ApiResponse;
import com.appshop.back_shop.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/categories")
@Tag(name = "Categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @PostMapping("create-categories")
    ApiResponse<Category> createCategory(@Valid @RequestBody CategoryRequest request){
        return ApiResponse.<Category>builder()
                .result(categoryService.createCategory(request))
                .code(200)
                .message("Create category successful")
                .build();
    }

    @GetMapping("list-categories")
    ApiResponse<List<Category>> getListCategories(){
        return ApiResponse.<List<Category>>builder()
                .result(categoryService.getListCategories())
                .build();
    }

    @GetMapping("/{categoryId}")
    ApiResponse<Category> getCategoryById(@PathVariable("categoryId") Long id){
        return ApiResponse.<Category>builder()
                .result(categoryService.getCategoryById(id))
                .code(200)
                .message("Get category successfully")
                .build();
    }

    @PutMapping("/update-category/{categoryId}")
    ApiResponse<Category> updateCategory(@Valid @RequestBody CategoryRequest request, @PathVariable("categoryId") Long id){
        return ApiResponse.<Category>builder()
                .result(categoryService.updateCategory(request, id))
                .code(200)
                .message("Update category successfully")
                .build();
    }

    @DeleteMapping("delete-category/{categoryId}")
    ApiResponse<Void> deleteCategory(@PathVariable("categoryId") Long id){
        categoryService.deleteCategory(id);
        return ApiResponse.<Void>builder()
                .code(200)
                .message("Category deleted successful")
                .build();
    }

    @GetMapping("/get-category-by-name/{categoryName}")
    ApiResponse<Category> getCategoryName(@PathVariable("categoryName") String name){
        return ApiResponse.<Category>builder()
                .code(200)
                .result(categoryService.getCategoryByName(name))
                .message("Get the name category successfully")
                .build();
    }
}
