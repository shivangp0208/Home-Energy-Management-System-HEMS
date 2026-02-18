package com.project.hems.auth_service_hems.exception;


public class UserNotFoundException extends RuntimeException {

    private final String userName;

    public UserNotFoundException(String userName){
        super("User not found:- "+userName);
        this.userName=userName;
    }

    public String getUserName() {
        return userName;
    }
}
