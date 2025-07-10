package com.newSystem.ProductManagementSystemImplemented.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserEntityNotFoundException extends UsernameNotFoundException {

    private final long serialVersionUID = 2;

    public UserEntityNotFoundException(String message){
        super(message);
    }
}
