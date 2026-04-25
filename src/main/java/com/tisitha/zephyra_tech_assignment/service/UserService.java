package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface UserService {

    AccountResponseDto getUser(Authentication authentication);

    void registerUserAccount(UserRegisterDto userRegisterDto);

    LoginResponseDto loginAccount(LoginDto loginDto, HttpServletResponse response);

    LoginResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response);

    void logoutAccount(HttpServletRequest request, HttpServletResponse response);

    void updateUser(UserUpdateDTO userUpdateDTO, Authentication authentication);

    void updatePassword(NewPasswordRequestDto newPasswordRequestDto, Authentication authentication);

    void deleteUser(PasswordDTO passwordDTO, Authentication authentication);

}
