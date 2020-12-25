package com.matus.expenzor.dto.expense;

import com.matus.expenzor.dto.user.UserDTO;
import com.matus.expenzor.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {

    @NotNull
    private Long id;

    @NotNull(message = "*Please provide date")
    private Date date;

    @NotNull(message = "*Please provide amount")
    private int value;

    @Nullable
    private Category category;

    @Nullable
    private String description;

    private UserDTO user;

}
