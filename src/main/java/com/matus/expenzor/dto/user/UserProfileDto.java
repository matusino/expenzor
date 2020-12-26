package com.matus.expenzor.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

    @NotNull
    private String username;

    @NotNull
    @Email
    private String email;

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
}
