package com.tisitha.zephyra_tech_assignment.exception;

public class OtpExpiredException extends RuntimeException{

    public OtpExpiredException(){
        super("Otp has Expired");
    }
}
