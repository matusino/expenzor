package com.matus.expenzor.repository;

import com.matus.expenzor.model.Expense;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
}
