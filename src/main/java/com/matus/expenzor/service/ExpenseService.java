package com.matus.expenzor.service;

import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Service
public interface ExpenseService {

    Expense addExpense(Expense expense);

    List<Expense> findByMonth(int month, int year, int userId);

    void deleteExpenseById(Long id);

    List<Expense> findAllUserExpenses(int userId, int year);

    void export(HttpServletResponse response, List<Expense> expenses) throws IOException;


}
