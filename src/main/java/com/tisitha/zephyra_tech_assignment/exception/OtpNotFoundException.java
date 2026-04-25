package com.tisitha.zephyra_tech_assignment.exception;

public class OtpNotFoundException extends ResourceNotFoundException{

    public OtpNotFoundException() {
        super("Cannot find OTP information");
    }
}
