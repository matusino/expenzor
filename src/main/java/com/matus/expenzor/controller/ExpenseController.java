package com.matus.expenzor.controller;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.mapper.ExpenseMapper;
import com.matus.expenzor.mapper.UserMapper;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.ExpenseService;
import com.matus.expenzor.service.UserService;
import com.matus.expenzor.utils.ExcelExporter;
import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final ExpenseMapper expenseMapper;
    private final UserMapper userMapper;

    @PostMapping("/add")
    public ResponseEntity addNewExpense(@Valid @RequestBody ExpenseDto expenseDto, BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        } else {
            Optional <User> user = userService.findByUserName(principal.getName());
            if(user.isPresent()){
                expenseDto.setUser(userMapper.userToDto(user.get()));
                expenseService.addExpense(expenseMapper.toExpense(expenseDto));
            }else {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all/{year}")
    public ResponseEntity getAllExpensesPerYearForLoggedUser(@PathVariable int year , Principal principal){
        List<Expense> expenses = expenseService.findAllUserExpenses(userService.fetchUserId(principal.getName()), year);
        if(!expenses.isEmpty()) {
            return new ResponseEntity<>(expenses, HttpStatus.OK);
        }
        return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/get/expenses/{month}/{year}/{username}")
    public ResponseEntity getExpensesByMonthForCurrentYear(@PathVariable int month, @PathVariable int year, @PathVariable String username) {
        Optional<User> user = userService.findByUserName(username);
        if (user.isPresent()) {
            List<Expense> expenses = expenseService.findByMonth(month, year, userService.fetchUserId(username));
            return new ResponseEntity<>(expenseMapper.expenseListToDto(expenses), HttpStatus.OK);
        }else
            return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteExpenseById(@PathVariable Long id){
        expenseService.deleteExpenseById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/export/excel/{month}/{year}")
    public void exportToExcel(@PathVariable int month, @PathVariable int year, HttpServletResponse response, Principal principal) throws IOException {
        response.setContentType("blob");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=expenses.xlsx";

        response.setHeader(headerKey, headerValue);

        if (month == 0){
            List<Expense> expenses = expenseService.findAllUserExpenses(userService.fetchUserId(principal.getName()), year);
            ExcelExporter excelExporter = new ExcelExporter(expenses);
            excelExporter.export(response);
        }else {
            List<Expense> expenses = expenseService.findByMonth(month, year, userService.fetchUserId(principal.getName()));
            ExcelExporter excelExporter = new ExcelExporter(expenses);

            excelExporter.export(response);
        }
    }
}
