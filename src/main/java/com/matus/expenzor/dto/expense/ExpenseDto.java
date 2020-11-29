package com.matus.expenzor.dto.expense;


import com.matus.expenzor.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
    private Long id;
    private Date date;
    private int value;
    private Category category;
    private String description;
}
