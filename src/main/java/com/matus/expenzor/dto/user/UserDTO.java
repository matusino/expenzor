package com.matus.expenzor.dto.user;

import com.matus.expenzor.dto.expense.ExpenseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @NotEmpty
    private Long id;

    @Nullable
    private String firstName;

    @Nullable
    private String lastName;

    @Nullable
    private String gender;

    @Nullable
    private String city;

    @Nullable
    private String phoneNumber;

    @Nullable
    private int age;

    @NotEmpty
    private String userName;

    @NotEmpty
    @Email
    private String emailAddress;

    private List<ExpenseDto> expenses = new ArrayList<>();

}
