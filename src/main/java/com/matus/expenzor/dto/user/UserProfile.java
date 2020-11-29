package com.matus.expenzor.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String userName;
    private String email;
    private String firstName;
    private String lastName;
    private String gender;
    private String city;
    private String phoneNumber;
    private int age;
    private String password;
    private String newPassword;
}
