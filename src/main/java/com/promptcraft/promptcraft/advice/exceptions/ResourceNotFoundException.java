package com.promptcraft.promptcraft.advice.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ResourceNotFoundException extends RuntimeException{
//    public ResourceNotFound(String message){
//        super(message);
//    }
    private final String resourceName;
    private final String resourceId;
}
