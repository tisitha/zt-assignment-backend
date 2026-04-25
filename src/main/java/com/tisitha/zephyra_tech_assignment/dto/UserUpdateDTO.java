package com.tisitha.zephyra_tech_assignment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String fname;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lname;

}
