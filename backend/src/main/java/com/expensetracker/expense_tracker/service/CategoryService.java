package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.dto.CategoryDTO;
import com.expensetracker.expense_tracker.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker.model.Category;
import com.expensetracker.expense_tracker.model.User;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public List<CategoryDTO> getAllCategories(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        List<Category> categories = categoryRepository.findByUserIdOrUserIsNullOrderByNameAsc(user.getId());
        
        return categories.stream().map(c -> CategoryDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .build()).collect(Collectors.toList());
    }

    public CategoryDTO createCategory(String email, CategoryDTO categoryDTO) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Category category = Category.builder()
                .name(categoryDTO.getName())
                .user(user)
                .build();
                
        category = categoryRepository.save(category);
        
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
