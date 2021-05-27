package com.matus.expenzor.service.impl;

import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.repository.ExpenseRepository;
import com.matus.expenzor.utils.ExcelExporter;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExcelExporter excelExporter;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    @Captor
    ArgumentCaptor<Expense> expenseArgumentCaptor;

    private LocalDate date = LocalDate.now();
    private Expense expense2;
    private Expense expense;

    @Before
    public void setupMock() {
        MockitoAnnotations.initMocks(this);
    }

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        expense = new Expense(1L, 10, "Test", date, Category.CLOTHING, new User());
        expense2= new Expense(2L, 20, "Test2", date, Category.FOOD, user);
    }

    @Test
    void shouldAddAnyNewExpense() {
        //given
        given(expenseRepository.save(any(Expense.class))).willReturn(expense);
        //when
        Expense savedExpense = expenseService.addExpense(expense);
        //then
        then(expenseRepository).should().save(any(Expense.class));
        verify(expenseRepository, Mockito.times(1)).save(expenseArgumentCaptor.capture());
        assertEquals(expenseArgumentCaptor.getValue().getId(), expense.getId());
    }

    @Test
    void shouldFindAllUserExpensesByIdAndYear() {
        //given
        List<Expense> expenseList = new ArrayList<>();
        int userId = Math.toIntExact(expense2.getUser().getId());
        int year = date.getYear();
        int month = date.getMonthValue();
        expenseList.add(expense2);
        given(expenseRepository.findExpenseByMonth(month, year, userId)).willReturn(expenseList);
        //when
        List<Expense> foundExpenses = expenseService.findByMonth(month, year, userId);
        //then
        then(expenseRepository).should().findExpenseByMonth(month, year, userId);
        assertNotNull(foundExpenses);
    }

    @Test
    void shouldFindExpenseByMonthAndYearUserId() {
        //given
        List<Expense> expenseList = new ArrayList<>();
        int userId = Math.toIntExact(expense2.getUser().getId());
        int year = LocalDateTime.now().getYear();
        expenseList.add(expense2);
        given(expenseRepository.findExpenseByUserId(userId, year)).willReturn(expenseList);

        //when
        List<Expense> foundExpenses = expenseService.findAllUserExpenses(userId, year);

        //then
        then(expenseRepository).should().findExpenseByUserId(userId, year);
        assertNotNull(foundExpenses);
    }

    @Test
    void shouldDeleteExpenseById() {
        //given
        //when
        expenseService.deleteExpenseById(1L);
        //then
        then(expenseRepository).should().deleteById(1L);
    }
}