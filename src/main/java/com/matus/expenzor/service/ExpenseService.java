package com.matus.expenzor.service;

import com.matus.expenzor.model.Expense;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExpenseService {

    Expense addExpense(Expense expense);

    List<Expense> findByMonth(int month, int year, int userId);

    void deleteExpenseById(Long id);

    List<Expense> findAllUserExpenses(int userId, int year);


}
