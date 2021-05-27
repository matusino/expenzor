package com.matus.expenzor.repository;

import com.matus.expenzor.BaseTest;
import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ExpenseRepositoryTest extends BaseTest{

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private Expense expense;
    private Expense expense2;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setPassword("testPassword");
        user.setEmailAddress("test@email.com");
        user.setUserName("testUserName");
        expense = Expense.builder()
                .value(10)
                .description("test")
                .date(LocalDate.now())
                .category(Category.CLOTHING)
                .user(user)
                .build();
        expense2 = Expense.builder()
                .value(20)
                .description("test2")
                .date(LocalDate.now())
                .category(Category.GROCERIES)
                .user(user)
                .build();
        userRepository.save(user);

    }

    @Test
    void shouldSaveExpense(){
        Expense savedExpense = expenseRepository.save(expense);
        assertThat(savedExpense).usingRecursiveComparison().ignoringFields("id").isEqualTo(expense);
    }

    @Test
    void shouldFindExpenseByMonthAndYearAndUserId(){
        expenseRepository.save(expense);
        expenseRepository.save(expense2);
        List<Expense> expenses = expenseRepository.findExpenseByMonth(LocalDateTime.now().getMonthValue()
                ,LocalDateTime.now().getYear()
                ,1);
        assertThat(expenses.size()).isEqualTo(2);
    }

    @Test
    void shouldFindExpenseByUserId(){
        expenseRepository.save(expense);
        List<Expense> expenses = expenseRepository.findExpenseByUserId(1,LocalDateTime.now().getYear());
        assertThat(expenses).isNotEmpty();
    }
}
