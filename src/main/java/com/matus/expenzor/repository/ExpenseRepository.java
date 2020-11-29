package com.matus.expenzor.repository;

import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    List<Expense> findByUserId(Long id);

    List<Expense> findByCategory(Category category);

    @Query("SELECT e FROM Expense e WHERE month(e.date) = :month AND user_id = :user_id")
    List<Expense> findExpenseByMonth(@Param("month") int month, @Param("user_id") int user_id);

}
