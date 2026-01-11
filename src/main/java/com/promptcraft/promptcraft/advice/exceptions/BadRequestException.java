package com.promptcraft.promptcraft.advice.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BadRequestException extends RuntimeException{

    private final String message;
}
