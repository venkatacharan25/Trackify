package com.expensetracker.expense_tracker.repository;

import com.expensetracker.expense_tracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserIdOrderByDateDesc(Long userId);
    
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
    List<Expense> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId ORDER BY e.date DESC")
    List<Expense> findByUserIdAndCategoryIdOrderByDateDesc(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.category.id = :categoryId AND e.date >= :startDate AND e.date <= :endDate ORDER BY e.date DESC")
    List<Expense> findByUserIdAndCategoryIdAndDateBetween(@Param("userId") Long userId, @Param("categoryId") Long categoryId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
