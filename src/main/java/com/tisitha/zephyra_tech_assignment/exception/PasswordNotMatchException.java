package com.tisitha.zephyra_tech_assignment.exception;

public class PasswordNotMatchException extends RuntimeException{

    public PasswordNotMatchException(){
        super("Passwords are not matching");
    }
}
