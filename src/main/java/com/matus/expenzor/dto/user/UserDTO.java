package com.matus.expenzor.dto.user;

import com.matus.expenzor.dto.expense.ExpenseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String gender;
    private String city;
    private String phoneNumber;
    private int age;
    private String userName;
    private String emailAddress;

    private List<ExpenseDto> expenses = new ArrayList<>();

}
