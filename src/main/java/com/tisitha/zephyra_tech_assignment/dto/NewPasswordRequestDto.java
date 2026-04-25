package com.tisitha.zephyra_tech_assignment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewPasswordRequestDto {

    @NotBlank(message = "New Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Repeat Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String passwordRepeat;

    @NotBlank(message = "Current Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String currentPassword;
}
