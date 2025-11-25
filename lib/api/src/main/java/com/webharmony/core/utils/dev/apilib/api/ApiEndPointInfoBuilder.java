package com.webharmony.core.utils.dev.apilib.api;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiEndPointInfoBuilder {

    public List<ApiEndpointInfo> getAllApiEndpoints() {
        final List<ApiEndpointInfo> resultList = new ArrayList<>();
        final Map<RequestMappingInfo, HandlerMethod> springMappings = getSpringMappings();
        final Set<Class<? extends AbstractBaseController>> controllers = ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractBaseController.class);
        for (Class<? extends AbstractBaseController> controllerClass : controllers.stream().sorted(Comparator.comparing(Class::getName)).toList()) {
            resultList.addAll(getHandlerMethodsByClass(controllerClass, springMappings));
        }

        return resultList;
    }

    private Map<RequestMappingInfo, HandlerMethod> getSpringMappings() {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = ContextHolder.getSpringContext()
                .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        return requestMappingHandlerMapping
                .getHandlerMethods();
    }

    private List<ApiEndpointInfo> getHandlerMethodsByClass(Class<? extends AbstractBaseController> controllerClass, Map<RequestMappingInfo, HandlerMethod> springMappings) {
        final List<ApiEndpointInfo> resultList = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> springEntry : springMappings.entrySet()) {
            final HandlerMethod handlerMethod = springEntry.getValue();
            if(controllerClass.equals(handlerMethod.getBeanType())) {
                final RequestMappingInfo mappingInfo = springEntry.getKey();
                for (RequestMethod requestMethod : mappingInfo.getMethodsCondition().getMethods()) {

                    final String methodName = requestMethod.name();
                    final String url = Optional.ofNullable(mappingInfo.getPathPatternsCondition()).orElseThrow()
                            .getPatterns().iterator().next().getPatternString();
                    final String id = String.format("%s_%s", methodName, url);

                    final Class<?> requestBodyType = Stream.of(handlerMethod.getMethodParameters())
                            .filter(p -> p.hasParameterAnnotation(RequestBody.class))
                            .findAny()
                            .map(MethodParameter::getParameterType)
                            .orElse(null);

                    final ApiEndpointInfo endpointInfo = new ApiEndpointInfo();
                    endpointInfo.setId(id);
                    endpointInfo.setControllerClass(controllerClass);
                    endpointInfo.setMethodName(handlerMethod.getMethod().getName());
                    endpointInfo.setRestMethod(methodName);
                    endpointInfo.setUrl(url);
                    endpointInfo.setRequestBodyType(requestBodyType);
                    endpointInfo.setResponseType(handlerMethod.getReturnType().getNestedGenericParameterType());
                    endpointInfo.setPathVariableTypes(getPathVariables(handlerMethod));
                    endpointInfo.setQueryParameterTypes(getQueryVariables(handlerMethod));
                    endpointInfo.setHandlerMethod(handlerMethod);

                    resultList.add(endpointInfo);
                }
            }
        }
        return resultList;
    }

    private Map<String, Class<?>> getPathVariables(HandlerMethod handlerMethod) {
        return Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(PathVariable.class))
                .collect(Collectors.toMap(p -> Optional.of(Objects.requireNonNull(p.getParameterAnnotation(PathVariable.class)).value()).filter(StringUtils::isNotNullAndNotEmpty).orElseThrow(() -> new InternalServerException(String.format("No path variable value set on %s", handlerMethod))), MethodParameter::getParameterType));
    }

    private Map<String, Class<?>> getQueryVariables(HandlerMethod handlerMethod) {
        return Stream.of(handlerMethod.getMethodParameters())
                .filter(p -> p.hasParameterAnnotation(RequestParam.class))
                .collect(Collectors.toMap(p -> Optional.of(Objects.requireNonNull(p.getParameterAnnotation(RequestParam.class)).value()).filter(StringUtils::isNotNullAndNotEmpty).orElseThrow(() -> new InternalServerException(String.format("No query parameter value set on %s", handlerMethod))), MethodParameter::getParameterType));
    }

}
