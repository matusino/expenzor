package com.matus.expenzor.mapper;

import com.matus.expenzor.dto.expense.ExpenseDto;
import com.matus.expenzor.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class},componentModel = "spring")
public interface ExpenseMapper {

    @Mapping(target = "user", ignore = true)
    ExpenseDto toExpenseDto(final Expense expense);

    List<ExpenseDto> expenseListToDto(final List<Expense> expenses);

    Expense toExpense(final ExpenseDto expenseDto);

    List<Expense> dtoToExpenseList(final List<ExpenseDto> expenseDto);

}
