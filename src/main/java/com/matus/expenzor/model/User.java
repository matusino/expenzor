package com.matus.expenzor.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.matus.expenzor.annotation.UniqueUsername;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "user",uniqueConstraints={@UniqueConstraint(columnNames={"user_name"})})
public class User extends BaseEntity{

    @NotNull
    @Column(name = "user_name")
    @Size(min = 6, max = 15)
    @UniqueUsername
    private String userName;

    @NotEmpty(message = "Please provide valid email address")
    @Email
    private String emailAddress;

    @Length(min = 5, message = "*Your password must have at least 5 characters")
    @NotEmpty(message = "*Please provide your password")
    private String password;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Expense> expenses = new ArrayList<>();

}
