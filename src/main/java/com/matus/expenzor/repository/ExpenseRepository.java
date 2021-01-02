package com.matus.expenzor.repository;

import com.matus.expenzor.model.Category;
import com.matus.expenzor.model.Expense;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {

    List<Expense> findByCategory(Category category);

    @Query("SELECT e FROM Expense e WHERE month(e.date) = :month AND year(e.date) = :year AND user_id = :user_id")
    List<Expense> findExpenseByMonth(@Param("month") int month, @Param("year") int year, @Param("user_id") int user_id);

    @Query("SELECT e FROM Expense e WHERE user_id = :user_id AND year(e.date) = :year")
    List<Expense> findExpenseByUserId(int user_id, @Param("year") int year);
}
