package com.matus.expenzor.controller;

import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import com.matus.expenzor.model.User;
import com.matus.expenzor.service.ExpenseService;
import com.matus.expenzor.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/expense")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    @PostMapping("/add")
    public ResponseEntity addNewExpense(@RequestBody Expense expense, Principal principal){
        User user = userService.findByUserName(principal.getName()).orElse(null);
        expense.setUser(user);

        expenseService.addExpense(expense);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-all")
    public ResponseEntity getExpensesByLoggedUser(Principal principal){
        User user = userService.findByUserName(principal.getName()).orElse(null);
        if(user == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if(user.getExpenses().isEmpty()){
            return new ResponseEntity<>(null,HttpStatus.OK);
        }else {
            return new ResponseEntity<>(user.getExpenses(),HttpStatus.OK);
        }
    }

    @GetMapping("/get/{category}")
    public ResponseEntity getExpenseByCategory(@PathVariable String category){
        List<Expense> expenses = expenseService.findByCategory(Category.valueOf(category.toUpperCase()));
        return new ResponseEntity<>(expenses, HttpStatus.OK);

    }

    @GetMapping("/get/expenses/{month}/{username}")
    public ResponseEntity getExpensesByMonth(@PathVariable int month, @PathVariable String username){
        List<Expense> expenses = expenseService.findByMonth(month, userService.fetchUserId(username));
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteExpenseById(@PathVariable Long id){
        expenseService.deleteExpenseById(id);
        return ResponseEntity.ok().build();
    }
}
