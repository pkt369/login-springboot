package com.developer.login.common.exception;

public class NoExistUserException extends RuntimeException {
    public NoExistUserException() {
        super("no exist user");
    }
}
