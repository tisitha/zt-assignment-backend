package com.tisitha.zephyra_tech_assignment.service;

import com.tisitha.zephyra_tech_assignment.dto.ChangePasswordDto;

public interface ForgotPasswordService {

    void verifyEmail(String email);

    void verifyOtp(Integer otp,String email);

    void changePasswordHandler(ChangePasswordDto changePasswordDto, Integer otp, String email);

}
