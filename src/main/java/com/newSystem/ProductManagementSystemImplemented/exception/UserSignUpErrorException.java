package com.newSystem.ProductManagementSystemImplemented.exception;

public class UserSignUpErrorException extends RuntimeException {

    private final long serialVersionUID = 10;

    public UserSignUpErrorException(String message){
        super(message);
    }
}
