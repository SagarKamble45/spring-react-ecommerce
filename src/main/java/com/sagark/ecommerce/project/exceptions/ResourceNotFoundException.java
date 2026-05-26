package com.sagark.ecommerce.project.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourceNotFoundException(String message, Throwable cause, String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field,fieldName) );
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException() {
    }

    public ResourceNotFoundException(String resourceName, String field, long fieldId){
        super(String.format("%s not found with %s: %s", resourceName, field,fieldId) );
        this.resourceName=resourceName;
        this.field=field;
        this.fieldId=fieldId;
    }
}
