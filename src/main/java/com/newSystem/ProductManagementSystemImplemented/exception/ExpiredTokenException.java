package com.newSystem.ProductManagementSystemImplemented.exception;

public class ExpiredTokenException extends IllegalArgumentException{

    private final long serialVersionUID = 13;

    public ExpiredTokenException(String message){super(message);}
}
