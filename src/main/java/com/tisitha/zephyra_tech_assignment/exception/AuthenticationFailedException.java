package com.tisitha.zephyra_tech_assignment.exception;

public class AuthenticationFailedException extends RuntimeException{

    public AuthenticationFailedException(String message){
        super(message);
    }

    public AuthenticationFailedException(){
        super("Authentication failed");
    }

}
