    package com.promptcraft.promptcraft.advice;

    import com.fasterxml.jackson.annotation.JsonInclude;
    import lombok.Data;

    import java.time.LocalDateTime;

    @Data
    public class ApiResponse<T> {

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private LocalDateTime timeStamp;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private T data;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        private ApiError error;

        public ApiResponse() {
            this.timeStamp = LocalDateTime.now();
        }

        public ApiResponse(T data) {
            this();
            this.data = data;
        }

        public ApiResponse(ApiError error) {
    //        this();
            this.error = error;
        }
    }
