package com.newSystem.ProductManagementSystemImplemented.exception;

public class PasswordResetTokenException extends RuntimeException {

    private final long serialVersionUID = 12;

    public PasswordResetTokenException(String message){
        super(message);
    }
}
