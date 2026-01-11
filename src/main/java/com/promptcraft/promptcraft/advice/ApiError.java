package com.promptcraft.promptcraft.advice;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.List;

public record ApiError(

        HttpStatus status,

        String message,

        Instant timestamp,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        List<ApiFieldError> errors

) {

    public ApiError(HttpStatus status, String message) {
        this(status, message, Instant.now(), null); // Sets a default timestamp
    }

    public ApiError(HttpStatus status, String message, List<ApiFieldError> errors) {
        this(status, message, Instant.now(), errors); // Sets a default timestamp
    }

}

record ApiFieldError(String field, String message) {

}
