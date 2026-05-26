package com.sagark.ecommerce.project.exceptions;

public class APIException extends RuntimeException{

    private static final long serialValidUID = 1l;

    public APIException(){

    }

    public APIException(String message){
        super(message);
    }
}
