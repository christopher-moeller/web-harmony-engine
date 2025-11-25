package com.webharmony.core.api.rest.error;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ApiError {

    public static final String TYPE_ERROR = "ERROR";

    public static final String TYPE_VALIDATION = "VALIDATION";

    private int status;
    private String statusText;
    private String message;

    @Setter
    private String type = TYPE_ERROR;

    @Setter
    private Object data;

    public ApiError(HttpStatus status, String message) {
        this.status = status.value();
        this.statusText = status.name();
        this.message = message;
    }

}
