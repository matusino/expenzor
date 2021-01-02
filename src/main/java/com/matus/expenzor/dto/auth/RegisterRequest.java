package com.matus.expenzor.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotNull(message = "*Please provide your username")
    @Size(min = 6, max = 15,message = "*Your username must have at least 6 characters")
    private String username;

    @NotNull(message = "*Please provide your username")
    @Email(message = "*Please provide valid email address")
    private String email;

    @Length(min = 6, max = 20, message = "*Your password must have at least 6 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;

}
