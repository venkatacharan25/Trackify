package com.expensetracker.expense_tracker.service;

import com.expensetracker.expense_tracker.dto.CategorySummaryDTO;
import com.expensetracker.expense_tracker.dto.ExpenseDTO;
import com.expensetracker.expense_tracker.dto.MonthlySummaryDTO;
import com.expensetracker.expense_tracker.exception.ResourceNotFoundException;
import com.expensetracker.expense_tracker.exception.UnauthorizedException;
import com.expensetracker.expense_tracker.model.Category;
import com.expensetracker.expense_tracker.model.Expense;
import com.expensetracker.expense_tracker.model.User;
import com.expensetracker.expense_tracker.repository.CategoryRepository;
import com.expensetracker.expense_tracker.repository.ExpenseRepository;
import com.expensetracker.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ExpenseDTO mapToDTO(Expense expense) {
        return ExpenseDTO.builder()
                .id(expense.getId())
                .title(expense.getTitle())
                .amount(expense.getAmount())
                .categoryId(expense.getCategory().getId())
                .categoryName(expense.getCategory().getName())
                .date(expense.getDate())
                .build();
    }

    public List<ExpenseDTO> getAllExpenses(String email) {
        User user = getUser(email);
        return expenseRepository.findByUserIdOrderByDateDesc(user.getId())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ExpenseDTO createExpense(String email, ExpenseDTO dto) {
        User user = getUser(email);
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Expense expense = Expense.builder()
                .title(dto.getTitle())
                .amount(dto.getAmount())
                .category(category)
                .date(dto.getDate() != null ? dto.getDate() : LocalDate.now())
                .user(user)
                .build();

        expense = expenseRepository.save(expense);
        return mapToDTO(expense);
    }

    public ExpenseDTO updateExpense(String email, Long id, ExpenseDTO dto) {
        User user = getUser(email);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to update this expense");
        }

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        expense.setTitle(dto.getTitle());
        expense.setAmount(dto.getAmount());
        expense.setCategory(category);
        expense.setDate(dto.getDate());

        expense = expenseRepository.save(expense);
        return mapToDTO(expense);
    }

    public void deleteExpense(String email, Long id) {
        User user = getUser(email);
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found"));

        if (!expense.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this expense");
        }

        expenseRepository.delete(expense);
    }

    public List<ExpenseDTO> filterExpenses(String email, Long categoryId, LocalDate startDate, LocalDate endDate) {
        User user = getUser(email);
        List<Expense> expenses;

        if (categoryId != null && startDate != null && endDate != null) {
            expenses = expenseRepository.findByUserIdAndCategoryIdAndDateBetween(user.getId(), categoryId, startDate, endDate);
        } else if (categoryId != null) {
            expenses = expenseRepository.findByUserIdAndCategoryIdOrderByDateDesc(user.getId(), categoryId);
        } else if (startDate != null && endDate != null) {
            expenses = expenseRepository.findByUserIdAndDateBetween(user.getId(), startDate, endDate);
        } else {
            expenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());
        }

        return expenses.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Map<String, Object> getSummary(String email) {
        User user = getUser(email);
        List<Expense> allExpenses = expenseRepository.findByUserIdOrderByDateDesc(user.getId());

        Double totalExpenses = allExpenses.stream().mapToDouble(Expense::getAmount).sum();

        // 1. Group records by month (YYYY-MM)
        Map<String, List<Expense>> monthlyGroups = allExpenses.stream()
                .collect(Collectors.groupingBy(e -> e.getDate().getYear() + "-" + String.format("%02d", e.getDate().getMonthValue())));

        // 2. Map each month to its details
        List<MonthlySummaryDTO> monthlySummary = monthlyGroups.entrySet().stream()
                .map(entry -> {
                    String monthStr = entry.getKey();
                    List<Expense> monthlyExpenses = entry.getValue();
                    
                    Double monthlyTotal = monthlyExpenses.stream().mapToDouble(Expense::getAmount).sum();
                    
                    // Category breakdown for THIS month
                    List<CategorySummaryDTO> catSummaryForMonth = monthlyExpenses.stream()
                            .collect(Collectors.groupingBy(e -> e.getCategory().getName()))
                            .entrySet().stream()
                            .map(catEntry -> new CategorySummaryDTO(catEntry.getKey(), catEntry.getValue().stream().mapToDouble(Expense::getAmount).sum()))
                            .collect(Collectors.toList());

                    return new MonthlySummaryDTO(monthStr, monthlyTotal, catSummaryForMonth);
                })
                .sorted((a, b) -> a.getMonth().compareTo(b.getMonth()))
                .collect(Collectors.toList());

        // Recent Expenses
        List<ExpenseDTO> recentExpenses = allExpenses.stream()
                .limit(5)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return Map.of(
                "totalAmount", totalExpenses,
                "expenseCount", allExpenses.size(),
                "monthlySummary", monthlySummary,
                "recentExpenses", recentExpenses
        );
    }
}
