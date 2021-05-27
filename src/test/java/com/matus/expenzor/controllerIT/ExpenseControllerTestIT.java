package com.matus.expenzor.controllerIT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matus.expenzor.BaseTest;
import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.repository.ExpenseRepository;
import com.matus.expenzor.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithUserDetails("testUserName")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ExpenseControllerTestIT extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @PostConstruct
    void initUserDetails(){
        User user = new User();
        user.setUserName("testUserName");
        user.setPassword("testPassword");
        user.setEmailAddress("test@email.com");
        userService.saveUser(user);
    }

    @Test
    void shouldAddNewExpense() throws Exception {
        ExpenseDto expenseDto;
        expenseDto = new ExpenseDto();
        expenseDto.setId(1L);
        expenseDto.setDate(LocalDate.now());
        expenseDto.setValue(10);

         mockMvc.perform(post("/expenses")
                 .accept(MediaType.APPLICATION_JSON)
                 .content(asJsonString(expenseDto))
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().is2xxSuccessful());
         Optional<Expense> expense = expenseRepository.findById(1L);
         assertThat(expense.get().getValue()).isEqualTo(expenseDto.getValue());
    }

    @Test
    void shouldReturn200WhenGetAllExpensesPerYearForLoggedUser() throws Exception {
        insertExpenseToDb();
        mockMvc.perform(get("/expenses/{year}", LocalDateTime.now().getYear())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].value", is(10)));
    }

    @Test
    void shouldGetExpensesByMonthForCurrentYear() throws Exception {
        insertExpenseToDb();
        mockMvc.perform(get("/expenses/{month}/{year}/{username}",
                LocalDateTime.now().getMonthValue(), LocalDateTime.now().getYear(),"testUserName")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].value", is(10)));
    }

    @Test
    void shouldDeleteExpenseById() throws Exception {
        insertExpenseToDb();
        mockMvc.perform(delete("/expenses/{id}", 1L))
                .andExpect(status().isOk());
        List<Expense> expenseList = new ArrayList<>();
        expenseRepository.findAll().forEach(expenseList::add);
        assertThat(expenseList).isEmpty();
    }

    @Test
    void shouldExportExpensesToXlsForMonthAndYear() throws Exception {
        insertExpenseToDb();
        mockMvc.perform(get("/expenses/xls/{month}/{year}", LocalDate.now().getMonthValue(),LocalDate.now().getYear())
                .contentType("application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=expenses.xlsx"))
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void insertExpenseToDb(){
        User user = new User();
        user.setId(1L);
        Expense expense;
        expense = new Expense();
        expense.setId(1L);
        expense.setDate(LocalDate.now());
        expense.setValue(10);
        expense.setCategory(Category.CLOTHING);
        expense.setUser(user);
        expenseRepository.save(expense);
    }
}
