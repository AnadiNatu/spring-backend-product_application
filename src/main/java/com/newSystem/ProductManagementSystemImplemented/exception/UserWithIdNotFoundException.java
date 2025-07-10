package com.newSystem.ProductManagementSystemImplemented.exception;

public class UserWithIdNotFoundException extends RuntimeException {

    private final long serialVersionUID = 13;
    public UserWithIdNotFoundException(String message) {
        super(message);
    }
}
