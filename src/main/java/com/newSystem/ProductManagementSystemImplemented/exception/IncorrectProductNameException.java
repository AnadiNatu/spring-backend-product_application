package com.newSystem.ProductManagementSystemImplemented.exception;

public class IncorrectProductNameException extends RuntimeException{

    private final long serialVersionUID = 7;

    public IncorrectProductNameException(String message){
        super(message);
    }
}
