package com.matus.expenzor.service.impl;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.model.Category;
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
    public List<Expense> findByUserId(Long id) {
        return expenseRepository.findByUserId(id);
    }

    @Override
    public List<Expense> findByCategory(Category category) {
        return expenseRepository.findByCategory(category);
    }

    @Override
    public List<Expense> findByMonth() {
        //
        return null;
    }

    @Override
    public List<ExpenseDto> findAll() {
        List<Expense> list = new ArrayList<>();
        List<ExpenseDto> listDto = new ArrayList<>();
        expenseRepository.findAll().forEach(list::add);
        for(Expense expense : list){
            ExpenseDto expenseDto = new ExpenseDto();
            expenseDto.setDescription(expense.getDescription());
            expenseDto.setValue(expense.getValue());
            expenseDto.setCategory(expense.getCategory());
            expenseDto.setDate(expense.getDate());
            listDto.add(expenseDto);
        }
        return listDto;
    }

    @Override
    public List<Expense> findByMonth(int month, int userId) {
        List<Expense> list = new ArrayList<>();
        expenseRepository.findExpenseByMonth(month, userId).forEach(list::add);
        return list;
    }

    @Override
    public void deleteExpenseById(Long id) {
        expenseRepository.deleteById(id);
    }
}
