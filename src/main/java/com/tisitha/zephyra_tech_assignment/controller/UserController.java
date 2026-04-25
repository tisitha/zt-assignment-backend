package com.tisitha.zephyra_tech_assignment.controller;

import com.tisitha.zephyra_tech_assignment.dto.*;
import com.tisitha.zephyra_tech_assignment.service.ForgotPasswordService;
import com.tisitha.zephyra_tech_assignment.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final ForgotPasswordService forgotPasswordService;

    public UserController(UserService userService, ForgotPasswordService forgotPasswordService) {
        this.userService = userService;
        this.forgotPasswordService = forgotPasswordService;
    }

    @GetMapping("/user/profile")
    public ResponseEntity<AccountResponseDto> getUser(Authentication authentication){
        return new ResponseEntity<>(userService.getUser(authentication),HttpStatus.OK);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Void> registerUserAccount(@Valid @RequestBody UserRegisterDto userRegisterDto){
        userService.registerUserAccount(userRegisterDto);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/c/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return new ResponseEntity<>(userService.refreshToken(request,response), HttpStatus.OK);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> loginAccount(@Valid @RequestBody LoginDto loginDto,HttpServletResponse response){
        return new ResponseEntity<>(userService.loginAccount(loginDto,response), HttpStatus.OK);
    }

    @PostMapping("/auth/c/logout")
    public ResponseEntity<Void> logoutAccount(HttpServletRequest request, HttpServletResponse response){
        userService.logoutAccount(request,response);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auth/verifymail/{email}")
    public ResponseEntity<Void> verifyEmail(@PathVariable String email){
        forgotPasswordService.verifyEmail(email);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/auth/verifyotp/{otp}/{email}")
    public ResponseEntity<Void> verifyOtp(@PathVariable Integer otp,@PathVariable String email){
        forgotPasswordService.verifyOtp(otp,email);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/auth/changepassword/{otp}/{email}")
    public ResponseEntity<Void> changePasswordHandler(@Valid @RequestBody ChangePasswordDto changePasswordDto, @PathVariable Integer otp, @PathVariable String email){
        forgotPasswordService.changePasswordHandler(changePasswordDto,otp,email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/user-update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO, Authentication authentication) {
        userService.updateUser(userUpdateDTO,authentication);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/password")
    public ResponseEntity<Void> updatePassword(@RequestBody NewPasswordRequestDto newPasswordRequestDto, Authentication authentication){
        userService.updatePassword(newPasswordRequestDto, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/user-delete")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody PasswordDTO pass, Authentication authentication) {
        userService.deleteUser(pass,authentication);
        return ResponseEntity.noContent().build();
    }

}
