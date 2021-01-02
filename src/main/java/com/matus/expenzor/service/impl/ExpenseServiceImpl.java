package com.matus.expenzor.service.impl;

import com.matus.expenzor.model.Expense;
import com.matus.expenzor.repository.ExpenseRepository;
import com.matus.expenzor.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Override
    public Expense addExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    public List<Expense> findAllUserExpenses(int userId, int year) {
        List<Expense> list = new ArrayList<>();
        expenseRepository.findExpenseByUserId(userId, year).forEach(list ::add);
        return list ;
    }

    @Override
    public List<Expense> findByMonth(int month, int year, int userId) {
        List<Expense> list = new ArrayList<>();
        expenseRepository.findExpenseByMonth(month,year, userId).forEach(list::add);
        return list;
    }

    @Override
    public void deleteExpenseById(Long id) {
        expenseRepository.deleteById(id);
    }
}
