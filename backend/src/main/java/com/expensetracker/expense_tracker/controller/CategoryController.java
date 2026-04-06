package com.expensetracker.expense_tracker.controller;

import com.expensetracker.expense_tracker.dto.CategoryDTO;
import com.expensetracker.expense_tracker.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(Authentication authentication) {
        return ResponseEntity.ok(categoryService.getAllCategories(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(Authentication authentication, @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.createCategory(authentication.getName(), categoryDTO), HttpStatus.CREATED);
    }
}
