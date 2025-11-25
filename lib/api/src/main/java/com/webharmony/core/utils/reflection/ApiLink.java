package com.webharmony.core.utils.reflection;

import com.webharmony.core.api.rest.controller.ApiController;
import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.MethodNotAllowed;
import com.webharmony.core.api.rest.controller.utils.RestRequestParams;
import com.webharmony.core.configuration.SpringWebConfiguration;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.utils.CollectionUtils;
import com.webharmony.core.utils.RegexUtils;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.reflection.proxy.ProxyExecutionMethod;
import com.webharmony.core.utils.reflection.proxy.ProxyMethodResult;
import com.webharmony.core.utils.reflection.proxy.ProxyUtils;
import com.webharmony.core.utils.tuple.Tuple2;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Getter
public class ApiLink {

    private static final String ID = "id";

    private final RequestMethod requestMethod;
    private final String link;
    private final List<ApiLinkPlaceholder> placeholders;

    private ApiLink(RequestMethod requestMethod, String link, List<ApiLinkPlaceholder> placeholders) {
        this.requestMethod = requestMethod;
        this.link = link;
        this.placeholders = placeholders;
    }

    public ApiLink resolveID(Object id) {
        return resolvePlaceholder(ID, Objects.toString(id));
    }

    public ApiLink resolvePlaceholder(String name, Object value) {
        String newLink = this.getLink().replace(String.format("{%s}", name), Objects.toString(value));
        List<ApiLinkPlaceholder> newPlaceholders = new ArrayList<>(this.getPlaceholders());
        newPlaceholders.removeIf(p -> p.name().equals(name));
        return new ApiLink(getRequestMethod(), newLink, newPlaceholders);
    }

    public ApiLink removeRequestParam(String name) {
        String newLink = this.getLink().replace(String.format("%s={%s}", name, name), "");
        if(newLink.endsWith("?"))
            newLink = newLink.replace("?", "");

        List<ApiLinkPlaceholder> newPlaceholders = new ArrayList<>(this.getPlaceholders());
        newPlaceholders.removeIf(p -> p.name().equals(name));
        return new ApiLink(getRequestMethod(), newLink, newPlaceholders);
    }


    public static <C> ApiLink of(Class<C> clazz, ProxyExecutionMethod<C, ?> method) {

        Class<C> controllerClass = AbstractBaseController.class.isAssignableFrom(clazz) ? findImplementationClassForController(clazz) : clazz;

        ProxyMethodResult result = ProxyUtils.proxyClazz(controllerClass).withMethod(method).build();
        return of(controllerClass, result.getMethod(), result.getArgs());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static <C> Class<C> findImplementationClassForController(Class<C> clazz) {
        ApplicationContext context = ContextHolder.getSpringContext();
        try {
            return (Class<C>) context.getBean(clazz).getClass();
        } catch (BeanCurrentlyInCreationException e) {
            final String beanClassName = ((AnnotationConfigServletWebServerApplicationContext) context).getBeanDefinition(Objects.requireNonNull(e.getBeanName())).getBeanClassName();
            return (Class<C>) Class.forName(beanClassName);
        }
    }

    public static ApiLink of(Class<?> clazz, Method method, Object[] paramValues) {
        Method mostSpecificMethod = ReflectionUtils.getMostSpecificMethodByClass(clazz, method);

        if(AnnotationUtils.findAnnotation(mostSpecificMethod, MethodNotAllowed.class) != null)
            return null;

        Tuple2<RequestMethod, String> linkFragments = createLink(clazz, mostSpecificMethod, paramValues);
        List<ApiLinkPlaceholder> placeholders = createPlaceholders(linkFragments.getType2(), mostSpecificMethod);
        ApiLink apiLink = of(linkFragments.getType1(), linkFragments.getType2(), placeholders);

        List<String> requestParamsToRemove = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for(int i=0; i<parameters.length; i++) {
            Parameter parameter = parameters[i];

            String name = parameter.getName();
            Object value = paramValues[i];
            if(value == null){
                if(placeholders.stream().anyMatch(p -> p.name().equals(name) && p.isOptional())) {
                    requestParamsToRemove.add(name);
                }
                continue;
            }

            apiLink = apiLink.resolvePlaceholder(name, value);
        }

        for(String paramToRemove : requestParamsToRemove)
            apiLink = apiLink.removeRequestParam(paramToRemove);

        return apiLink;
    }

    public static ApiLink of(RequestMethod requestMethod, String link, List<ApiLinkPlaceholder> placeholders) {
        return new ApiLink(requestMethod, link, placeholders);
    }

    private static List<ApiLinkPlaceholder> createPlaceholders(String link, Method mostSpecificMethod) {

        List<ApiLinkPlaceholder> resultList = new ArrayList<>();

        List<String> placeholdersInLink = RegexUtils.getStringsSurroundedBy(link, "{", "}");
        for(Parameter parameter : mostSpecificMethod.getParameters()) {
            PathVariable pathVariable = parameter.getDeclaredAnnotation(PathVariable.class);
            RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);

            final String placeholderVariableName;
            boolean isOptional = false;
            if(pathVariable != null) {
                placeholderVariableName = Optional.of(pathVariable.value()).filter(v -> !v.isEmpty()).orElseGet(parameter::getName);
            } else if(requestParam != null) {
                placeholderVariableName = Optional.of(requestParam.value()).filter(v -> !v.isEmpty()).orElseGet(parameter::getName);
                isOptional = !requestParam.required();
            } else {
                continue;
            }

            if(placeholdersInLink.contains(placeholderVariableName)) {
                resultList.add(new ApiLinkPlaceholder(parameter.getType(), placeholderVariableName, isOptional));
            }
        }

        return resultList;
    }

    private static Tuple2<RequestMethod, String> createLink(Class<?> clazz, Method mostSpecificMethod, Object... paramValues) {
        var linkList = getLinksByApiMethod(clazz, mostSpecificMethod, paramValues);
        Assert.hasSize(linkList, 1).verify();
        Map<RequestMethod, String> map = linkList.iterator().next();
        Assert.hasSize(map, 1).verify();
        var entry = map.entrySet().iterator().next();
        return Tuple2.of(entry.getKey(), entry.getValue());
    }

    private static List<Map<RequestMethod, String>> getLinksByApiMethod(Class<?> clazz, Method method, Object... paramValues) {

        RequestMapping requestMappingAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
        if(requestMappingAnnotation == null)
            return Collections.emptyList();

        List<Map<RequestMethod, String>> resultList = new ArrayList<>();
        for(RequestMethod requestMethod : CollectionUtils.toList(requestMappingAnnotation.method())) {
            List<String> values = getValuesByMethod(method, requestMethod, requestMappingAnnotation);
            if(values.isEmpty()) {
                resultList.add(buildResourceLink(clazz, "", requestMethod, getRequestParamsByMethod(method, paramValues)));
            } else {
                values.forEach(v ->  resultList.add(buildResourceLink(clazz, v, requestMethod, getRequestParamsByMethod(method, paramValues))));
            }
        }
        return resultList;
    }

    private static String getRequestParamsByMethod(Method method, Object[] paramValues) {
        final List<String> requestParamStrings = new ArrayList<>();

        int paramIndex = 0;
        for(Parameter parameter : method.getParameters()) {
            if(RestRequestParams.class.isAssignableFrom(parameter.getType())) {
                RestRequestParams restRequestParams = (RestRequestParams) paramValues[paramIndex];
                if(restRequestParams != null) {
                    restRequestParams.getNativeParameters()
                            .entrySet()
                            .stream()
                            .map(e -> String.format(String.format("%s=%s", e.getKey(), e.getValue())))
                            .forEach(requestParamStrings::add);
                }
                paramIndex++;
            } else {
                RequestParam requestParam = parameter.getDeclaredAnnotation(RequestParam.class);
                if(requestParam == null) {
                    paramIndex++;
                } else {
                    String name = Optional.of(requestParam).map(RequestParam::value).filter(v -> !v.isEmpty()).orElseGet(parameter::getName);
                    requestParamStrings.add(String.format("%s={%s}", name, name));
                }
            }
        }
        return String.join("&", requestParamStrings);
    }

    private static Map<RequestMethod, String> buildResourceLink(Class<?> clazz, String pathOfMethod, RequestMethod requestMethod, String requestParams){
        return Map.of(requestMethod, buildFullUrlByMethodPathAndMostSpecificClass(pathOfMethod, clazz, requestParams));
    }

    private static String buildFullUrlByMethodPathAndMostSpecificClass(String methodPath, Class<?> mostSpecificClass, String requestParams) {
        String url = String.format("%s/%s/%s", getBaseMapping(), getUrlForController(mostSpecificClass), methodPath);
        if(url.endsWith("/"))
            url = url.substring(0, url.length() - 1);

        if(!requestParams.isEmpty())
            url = url + "?" + requestParams;


        return url.replace("//", "/");
    }

    private static String getUrlForController(Class<?> mostSpecificControllerClass) {
        final String linkByApiLinkAnnotation = Optional.ofNullable(AnnotationUtils.findAnnotation(mostSpecificControllerClass, ApiController.class))
                .map(ApiController::value)
                .map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .findFirst()
                .orElse(null);

        return Objects.requireNonNullElseGet(linkByApiLinkAnnotation, () -> Optional.ofNullable(AnnotationUtils.findAnnotation(mostSpecificControllerClass, RequestMapping.class))
                .map(RequestMapping::value)
                .map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .findFirst()
                .orElse(""));

    }

    private static String getBaseMapping() {
        return SpringWebConfiguration.BASE_MAPPING;
    }

    private static List<String> getValuesByMethod(Method method, RequestMethod requestMethod, RequestMapping requestMappingAnnotation) {
        String[] values = null;
        if(requestMethod.equals(RequestMethod.GET))
            values = Optional.ofNullable(AnnotationUtils.findAnnotation(method, GetMapping.class)).map(GetMapping::value).orElse(null);
        else if(requestMethod.equals(RequestMethod.POST))
            values = Optional.ofNullable(AnnotationUtils.findAnnotation(method, PostMapping.class)).map(PostMapping::value).orElse(null);
        else if(requestMethod.equals(RequestMethod.PUT))
            values = Optional.ofNullable(AnnotationUtils.findAnnotation(method, PutMapping.class)).map(PutMapping::value).orElse(null);
        else if(requestMethod.equals(RequestMethod.DELETE))
            values = Optional.ofNullable(AnnotationUtils.findAnnotation(method, DeleteMapping.class)).map(DeleteMapping::value).orElse(null);

        return Optional.of(CollectionUtils.toList(values))
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> CollectionUtils.toList(requestMappingAnnotation.value()));
    }


}
