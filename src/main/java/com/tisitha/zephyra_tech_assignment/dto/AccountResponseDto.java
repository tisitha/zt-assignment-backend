package com.tisitha.zephyra_tech_assignment.dto;

import com.tisitha.zephyra_tech_assignment.model.Provider;
import com.tisitha.zephyra_tech_assignment.model.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class AccountResponseDto {

    private UUID id;

    private String fname;

    private String lname;

    private String email;

    private String profile;

    private Role role;

    private Provider provider;

}
