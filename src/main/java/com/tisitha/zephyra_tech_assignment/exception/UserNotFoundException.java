package com.tisitha.zephyra_tech_assignment.exception;

public class UserNotFoundException extends ResourceNotFoundException{

    public UserNotFoundException(){
        super("Cannot find user information");
    }
}
