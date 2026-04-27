package com.promptcraft.promptcraft.advice.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BadRequestException extends RuntimeException{

    //without a @Getter, in GlobalExceptionHandler, ex.getMessage() uses the wrong method from java.long.throwable class instead of this Error's class, and we end up receiveing null in response on client side
    private final String message;

}
