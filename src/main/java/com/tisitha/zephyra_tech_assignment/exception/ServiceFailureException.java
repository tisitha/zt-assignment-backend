package com.tisitha.zephyra_tech_assignment.exception;

public class ServiceFailureException extends RuntimeException{

    public ServiceFailureException(){
        super("Service Unavailable");
    }
}
