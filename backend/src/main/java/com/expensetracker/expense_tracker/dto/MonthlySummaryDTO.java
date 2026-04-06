package com.expensetracker.expense_tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlySummaryDTO {
    private String month;
    private Double totalAmount;
    private List<CategorySummaryDTO> categorySummary;
}
