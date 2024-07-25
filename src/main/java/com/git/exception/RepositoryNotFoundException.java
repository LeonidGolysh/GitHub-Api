package com.git.exception;

public class RepositoryNotFoundException extends RuntimeException{
    public RepositoryNotFoundException(String message) {
        super(message);
    }
}
