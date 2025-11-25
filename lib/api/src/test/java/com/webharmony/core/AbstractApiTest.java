package com.webharmony.core;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.error.ApiErrorWithJavaExceptionInstance;
import com.webharmony.core.api.rest.error.validation.ValidationFieldErrorDto;
import com.webharmony.core.api.rest.error.validation.ValidationResultDto;
import com.webharmony.core.utils.JacksonUtils;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.core.utils.reflection.proxy.ProxyExecutionMethod;
import com.webharmony.core.utils.reflection.proxy.ProxyMethodResult;
import com.webharmony.core.utils.reflection.proxy.ProxyUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Getter
public abstract class AbstractApiTest<C extends AbstractBaseController> extends AbstractBaseTest {

    @Autowired
    @SuppressWarnings("all")
    private C controller;

    public <R> R assertOkResponse(Function<C, ResponseEntity<R>> apiFunction) {
        ResponseEntity<R> responseEntity = apiFunction.apply(controller);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        return responseEntity.getBody();
    }

    protected void validateApiLink(ApiLink apiLink, RequestMethod requestMethod, String link) {
        assertThat(apiLink.getRequestMethod()).isEqualTo(requestMethod);
        assertThat(apiLink.getLink()).isEqualTo(link);
    }

    protected ValidationResultDto executeMethodAsRestCallAndExpectValidationError(ProxyExecutionMethod<C, ?> proxyMethod) {
        ApiErrorWithJavaExceptionInstance apiErrorWithJavaExceptionInstance = executeMethodAsRestCall(proxyMethod, ApiErrorWithJavaExceptionInstance.class, HttpStatus.BAD_REQUEST);
        return JacksonUtils.createDefaultJsonMapper().convertValue(apiErrorWithJavaExceptionInstance.getData(), ValidationResultDto.class);
    }

    protected <T> T executeMethodAsRestCall(ProxyExecutionMethod<C, ?> proxyMethod, Class<T> responseType, HttpStatus exceptStatus) {
        @SuppressWarnings("unchecked")
        final Class<C> controllerClass = (Class<C>) controller.getClass();
        ProxyMethodResult proxyMethodResult = ProxyUtils.proxyClazz(controllerClass).withMethod(proxyMethod).build();
        ApiLink apiLink = ApiLink.of(controllerClass, proxyMethodResult.getMethod(), proxyMethodResult.getArgs());

        final Object body = getRequestBoyByProxyMethodResult(proxyMethodResult);

        assert apiLink != null;
        final HttpMethod httpMethod = HttpMethod.valueOf(apiLink.getRequestMethod().name());

        return executeHttpRequest(httpMethod, apiLink.getLink(), body, responseType, exceptStatus);
    }

    private Object getRequestBoyByProxyMethodResult(ProxyMethodResult proxyMethodResult) {
        return getIndexOfRequestBodyParameter(proxyMethodResult.getMethod())
                .map(index -> proxyMethodResult.getArgs()[index])
                .orElse(null);
    }

    private Optional<Integer> getIndexOfRequestBodyParameter(Method method) {
        final Parameter[] parameters = method.getParameters();
        for(int i = 0; i < parameters.length; i++) {
            final Parameter parameter = parameters[i];
            if(parameter.getAnnotation(RequestBody.class) != null) {
                return Optional.of(i);
            }
        }
        return Optional.empty();
    }

    protected void assertValidationResultAsFieldError(ValidationResultDto validationResultDto, String fieldPath, String expectedErrorType) {
        final ValidationFieldErrorDto field = findValidationFieldByPath(validationResultDto, fieldPath);
        assertThat(field.getErrorMessages()).anyMatch(e -> e.getValidationName().equals(expectedErrorType));
    }

    protected ValidationFieldErrorDto findValidationFieldByPath(ValidationResultDto validationResult, String fieldPath) {
        return validationResult.getFields().stream()
                .filter(field -> field.getFieldPath().equals(fieldPath))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Field with path '%s' not found", fieldPath)));
    }
}
