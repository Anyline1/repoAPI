package ru.anyline.repoapi.exceptions;

public class ProjectNotFoundException extends RuntimeException{

    public ProjectNotFoundException(String message){
        super(message);
    }
}
