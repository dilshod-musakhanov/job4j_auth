package ru.job4j.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.auth.handlers.Operation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDto {
    @NotNull(message = "Id is mandatory", groups = Operation.OnUpdatePassword.class)
    @Positive
    private int id;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, message = "Password must have at least 3 characters")
    private String password;
}
