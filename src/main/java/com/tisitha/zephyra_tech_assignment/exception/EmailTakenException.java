package com.tisitha.zephyra_tech_assignment.exception;

public class EmailTakenException extends RuntimeException{

    public EmailTakenException(){
        super("Email is taken");
    }
}
