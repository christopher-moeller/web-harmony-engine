package com.webharmony.core.api.rest.error;

import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.enums.ECoreActorRight;
import com.webharmony.core.service.data.validation.ValidatorValidationException;
import com.webharmony.core.utils.exceptions.ApplicationException;
import com.webharmony.core.utils.exceptions.ExceptionHelper;
import com.webharmony.core.utils.exceptions.utils.HarmonyExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private static final HttpStatus DEFAULT_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;
    private static final String DEFAULT_ERROR_MESSAGE = "Unknown error";

    @ExceptionHandler(Throwable.class)
    protected ResponseEntity<ApiError> handleError(Throwable throwable) {
        HarmonyExceptionHandler.handle(throwable);
        if(logException(throwable)) {
            log.error(throwable.getMessage(), throwable);
        }
        final ApiError apiError = buildApiError(throwable);
        return ResponseEntity.status(apiError.getStatus()).body(apiError);
    }

    private boolean logException(Throwable throwable) {
        if(throwable instanceof ApplicationException e) {
            return e.shouldLogException();
        } else {
            return true;
        }
    }

    private ApiError buildApiError(Throwable throwable) {
        final HttpStatus status = getStatus(throwable);
        final String message = getErrorMessage(throwable);

        ApiError apiError = isUserAllowedToReadJavaException() ? new ApiErrorWithJavaExceptionInstance(status, message, buildApiJavaException(throwable)) : new ApiError(status, message);

        if (throwable instanceof ValidatorValidationException e) {
            apiError.setData(new ValidationResultDto(e.getValidationErrors()));
            apiError.setType(ApiError.TYPE_VALIDATION);
        }

        return apiError;
    }

    private HttpStatus getStatus(Throwable throwable) {
        if (throwable instanceof ApplicationException applicationException) {
            return applicationException.getHttpStatus();
        }
        return DEFAULT_STATUS;
    }

    private String getErrorMessage(Throwable throwable) {
        return isUserAllowedToReadMessage() ? getThrowableMessageRecursive(throwable) : DEFAULT_ERROR_MESSAGE;
    }

    private String getThrowableMessageRecursive(Throwable throwable) {
        String message = throwable.getMessage();
        if (message != null) {
            return message;
        } else {
            Throwable cause = throwable.getCause();
            if(cause != null) {
                return getThrowableMessageRecursive(cause);
            } else {
                return null;
            }
        }
    }

    private boolean isUserAllowedToReadMessage() {
        return ContextHolder.getContext().currentActorAsRight(ECoreActorRight.CORE_ERROR_MESSAGE_DETAIL_READ);
    }

    private boolean isUserAllowedToReadJavaException() {
        return ContextHolder.getContext().currentActorAsRight(ECoreActorRight.CORE_ERROR_JAVA_STACKTRACE_READ);
    }

    private ApiJavaExceptionInstance buildApiJavaException(Throwable throwable) {
        return new ApiJavaExceptionInstance(throwable.getMessage(), ExceptionHelper.buildStacktraceText(throwable));
    }

}
