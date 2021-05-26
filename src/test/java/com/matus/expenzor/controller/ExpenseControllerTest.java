package com.matus.expenzor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.dto.user.UserDTO;
import com.matus.expenzor.mapper.ExpenseMapper;
import com.matus.expenzor.mapper.UserMapper;
import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.security.JwtProvider;
import com.matus.expenzor.service.ExpenseService;
import com.matus.expenzor.service.UserService;
import com.matus.expenzor.service.impl.ExpenseServiceImpl;
import com.matus.expenzor.service.impl.UserDetailsServiceImpl;
import com.matus.expenzor.utils.ExcelExporter;
import io.swagger.models.auth.In;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ExpenseService expenseService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private ExpenseMapper expenseMapper;

    @MockBean
    private UserMapper userMapper;

    @Captor
    ArgumentCaptor<Expense> captor;

    private ExpenseDto expenseDto;

    @BeforeEach
    void setUp(){
        expenseDto = new ExpenseDto();
        expenseDto.setId(1L);
        expenseDto.setDate(LocalDate.now());
        expenseDto.setValue(10);
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldAddNewExpense() throws Exception {
        //given
        given(userService.findByUserName(("username"))).willReturn(Optional.of(new User()));

        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expenseDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(expenseService, times(1)).addExpense(expenseMapper.toExpense(expenseDto));
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldFailAddNewExpenseMissingUser() throws Exception {
        //given
        given(userService.findByUserName(("username"))).willReturn(Optional.empty());

        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expenseDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldFailAddNewExpenseInvalidInput() throws Exception {
        //given
        expenseDto.setDate(null);
        given(userService.findByUserName(("username"))).willReturn(Optional.of(new User()));

        mockMvc.perform(post("/expenses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expenseDto))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("*Please provide date")))
                .andExpect(status().is4xxClientError());
    }



    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldGetAllExpensesPerYearForLoggedUser() throws Exception {
        //given
        Expense expense = new Expense();
        expense.setValue(10);
        List<Expense> expenses = Arrays.asList(expense);
        given(expenseService.findAllUserExpenses(any(Integer.class), any(Integer.class))).willReturn(expenses);

        mockMvc.perform(get("/expenses/" + 2020)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].value", is(10)));
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldReturnNullNoExpenses() throws Exception {
        mockMvc.perform(get("/expenses/" + 2019)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void getExpensesByMonthForCurrentYear() throws Exception {
        //given
        Expense expense = new Expense();
        expense.setValue(10);
        List<Expense> expenses = Arrays.asList(expense);
        List<ExpenseDto> expensesDto = Arrays.asList(expenseDto);

        given(userService.findByUserName(any(String.class))).willReturn(Optional.of(new User()));
        given(expenseService.findByMonth(1,2021,1)).willReturn(expenses);
        given(userService.fetchUserId(any(String.class))).willReturn(1);
        given(expenseMapper.expenseListToDto(anyList())).willReturn(expensesDto);

        MvcResult response = mockMvc.perform(get("/expenses/{month}/{year}/{username}", 1,2021,"username")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].value", is(10))).andReturn();

        verify(expenseService, times(1)).findByMonth(1,2021,1);
        verify(expenseMapper, times(1)).expenseListToDto(expenses);
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldFailToGetExpensesByMonthForCurrentYear() throws Exception {
        mockMvc.perform(get("/expenses/{month}/{year}/{username}", 1,2021,"test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username="username",password = "pass")
    void deleteExpenseById() throws Exception {
        mockMvc.perform(delete("/expenses/{id}", 1L))
                .andExpect(status().isOk());

        verify(expenseService,times(1)).deleteExpenseById(1L);
    }

    @Test
    @WithMockUser(username="username",password = "pass")
    void shouldExportToExcelAllUserExpenses() throws Exception {
        given(userService.fetchUserId(any(String.class))).willReturn(1);

        MvcResult result = mockMvc.perform(get("/expenses/xls/{month}/{year}", 0,2021)
                .contentType("application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=expenses.xlsx"))
                .andExpect(status().isOk()).andReturn();

        then(expenseService).should().findAllUserExpenses(1,2021);
    }

    @Test
    @DisplayName("Should return all expenses for January(1)")
    @WithMockUser(username="username",password = "pass")
    void shouldExportToExcelAllUserExpensesByMonth() throws Exception {
        given(userService.fetchUserId(any(String.class))).willReturn(1);

        MvcResult result = mockMvc.perform(get("/expenses/xls/{month}/{year}", 1,2021)
                .contentType("application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=expenses.xlsx"))
                .andExpect(status().isOk()).andReturn();

        then(expenseService).should().findByMonth(1,2021, 1);
    }

    public static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}