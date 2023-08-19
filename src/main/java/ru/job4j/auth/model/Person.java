package ru.job4j.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.job4j.auth.handlers.Operation;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull(message = "Id must be non null", groups = {Operation.OnDelete.class, Operation.OnUpdate.class})
    private int id;

    @EqualsAndHashCode.Include
    @NotBlank(message = "Login is mandatory")
    @Size(min = 3, message = "Password must have at least 3 characters")
    private String login;

    @EqualsAndHashCode.Include
    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, message = "Password must have at least 3 characters")
    private String password;


}