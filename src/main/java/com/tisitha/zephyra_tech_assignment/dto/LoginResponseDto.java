package com.tisitha.zephyra_tech_assignment.dto;

import com.tisitha.zephyra_tech_assignment.model.Provider;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponseDto {

    private UUID id;

    private String name;

    private String accessToken;

    private String role;

    private String email;

    private Provider provider;

}
