package com.tisitha.zephyra_tech_assignment.dto;

import com.tisitha.zephyra_tech_assignment.model.Provider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private UUID id;

    private String fname;

    private String lname;

    private String email;

    private String profile;

    private Provider provider;

}
