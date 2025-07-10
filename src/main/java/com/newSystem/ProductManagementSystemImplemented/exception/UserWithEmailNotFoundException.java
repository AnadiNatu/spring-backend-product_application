package com.newSystem.ProductManagementSystemImplemented.exception;

public class UserWithEmailNotFoundException extends RuntimeException{

    private final long serialVersionUID = 11;

    public UserWithEmailNotFoundException(String message){
        super(message);
    }
}
