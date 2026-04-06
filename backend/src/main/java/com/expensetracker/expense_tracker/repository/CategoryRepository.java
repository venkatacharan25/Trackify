package com.expensetracker.expense_tracker.repository;

import com.expensetracker.expense_tracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrderByNameAsc(Long userId);
    List<Category> findByUserIdOrUserIsNullOrderByNameAsc(Long userId);
    Optional<Category> findByIdAndUserId(Long id, Long userId);
}
