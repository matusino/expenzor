package com.matus.expenzor.controller;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.mapper.ExpenseMapper;
import com.matus.expenzor.mapper.UserMapper;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.ExpenseService;
import com.matus.expenzor.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity addNewExpense(@Valid @RequestBody ExpenseDto expenseDto, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            log.error(errors.toString());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userService.findByUserName(principal.getName());
        if (user.isPresent()) {
            expenseDto.setUser(userMapper.userToDto(user.get()));
            expenseService.addExpense(expenseMapper.toExpense(expenseDto));
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            log.debug("User with username " + principal.getName() + " not found");
            return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{year}")
    public ResponseEntity getAllExpensesForYearForLoggedUser(@PathVariable int year, Principal principal) {
        List<Expense> expenses = expenseService.findAllUserExpenses(userService.fetchUserId(principal.getName()), year);
        if (expenses.isEmpty()) {
            log.debug("No Expenses for user " + principal.getName() + " for year " + year);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping("/{month}/{year}/{username}")
    public ResponseEntity getExpensesByMonthForCurrentYear(@PathVariable int month, @PathVariable int year, @PathVariable String username, Principal principal) {
        if (!username.equalsIgnoreCase(principal.getName())) {
            log.error("Invalid user name" + username);
            return ResponseEntity.badRequest().build();
        }
        Optional<User> user = userService.findByUserName(username);
        if (user.isPresent()) {
            List<Expense> expenses = expenseService.findByMonth(month, year, userService.fetchUserId(username));
            return new ResponseEntity<>(expenseMapper.expenseListToDto(expenses), HttpStatus.OK);
        } else
            log.debug("No Expenses for user " + principal.getName() + " for year " + year + month);
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteExpenseById(@PathVariable Long id) {
        try {
            expenseService.deleteExpenseById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/xls/{month}/{year}")
    public void exportToExcel(@PathVariable int month, @PathVariable int year, HttpServletResponse response, Principal principal) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses.xlsx";

        response.setHeader(headerKey, headerValue);

        if (month == 0) {
            List<Expense> expenses = expenseService.findAllUserExpenses(userService.fetchUserId(principal.getName()), year);
            expenseService.export(response, expenses);
        } else {
            List<Expense> expenses = expenseService.findByMonth(month, year, userService.fetchUserId(principal.getName()));
            expenseService.export(response, expenses);
        }
    }
}
