package com.example.task5.exception;

public class AccountAlreadyExistsException extends ApplicationException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }
}