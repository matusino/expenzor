package com.matus.expenzor.service;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExpenseService {

    Expense addExpense(Expense expense);

    List<Expense> findByUserId(Long id);

    List<Expense> findByCategory(Category category);

    List<Expense> findByMonth();

    List<ExpenseDto> findAll();

    List<Expense> findByMonth(int month, int userId);

    void deleteExpenseById(Long id);
}
