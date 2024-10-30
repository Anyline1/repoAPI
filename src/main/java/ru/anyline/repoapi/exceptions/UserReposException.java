package ru.anyline.repoapi.exceptions;

public class UserReposException extends RuntimeException {
    public UserReposException(String message) {
        super(message);
    }

    public UserReposException(String message, Throwable cause) {
        super(message, cause);
    }
}

