package com.webharmony.core.service.data.validation;

import com.webharmony.core.configuration.EProfile;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.service.data.validation.utils.*;
import com.webharmony.core.utils.StringUtils;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.ReflectionException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Validator<R> {

    @Getter
    private final Class<R> rootClass;

    @Getter
    @Setter
    private List<ValidationRule<R>> validationRules;

    private final ApplicationContext applicationContext;

    public Validator(Class<R> rootClass, ApplicationContext applicationContext) {
        this.rootClass = rootClass;
        this.applicationContext = applicationContext;
        this.validationRules = new ArrayList<>();
    }

    public void validate(R source) {
        List<ValidationAction<R>> validationActions = buildEffectiveValidationActions(source);
        List<ValidationActionResult<R>> validationActionResults = executeActions(validationActions, applicationContext.getBean(ValidatorService.class));
        List<ValidationError<?>> validationErrors = buildValidationErrors(validationActionResults);

        if(!validationErrors.isEmpty())
            throw new ValidatorValidationException(source, validationErrors);
    }

    private List<ValidationActionResult<R>> executeActions(List<ValidationAction<R>> actions, ValidatorService validatorService) {
        final List<ValidationActionResult<R>> validationActionResults = new ArrayList<>();
        for(ValidationAction<R> action : actions) {
            List<ValidationInterfaceResult<R>> interfaceResults = new ArrayList<>();
            List<NamedValidationInterface<?, R>> validationInterfaces = action.validationInterfaces();
            for(NamedValidationInterface<?, R> validationInterface : validationInterfaces) {
                var config  = validatorService.getValidationInterfaceConfig(validationInterface);
                if(!config.getIsActive())
                    continue;

                executeAction(action, validationInterface, config, interfaceResults);
            }

            ValidationActionResult<R> validationActionResult = new ValidationActionResult<>(action.rootSource(), interfaceResults);
            validationActionResults.add(validationActionResult);
        }

        return validationActionResults;
    }

    private void executeAction(ValidationAction<R> action, NamedValidationInterface<?, R> validationInterface, NamedValidationInterface.NamedValidationInterfaceConfiguration config, List<ValidationInterfaceResult<R>> interfaceResults) {
        ValidationContext<R> validationContext = createValidationContextByAction(action, validationInterface.getName());
        ValidationInterfaceResult<R> interfaceResult = new ValidationInterfaceResult<>(action.rootSource(), action.path(), validationInterface.getName());

        if(config.getAlwaysReturnInvalid()) {
            interfaceResult.extendResultByException(new ValidationIsMarkedAsAlwaysInvalidException());
        }

        try {
            validationInterface.validateUntyped(action.target(), validationContext);
        }catch (Exception e) {
            interfaceResult.extendResultByException(e);
            if(ContextHolder.getContext().isProfileActive(EProfile.DEV)) {
                log.error(e.getMessage(), e);
            }
        } finally {
            interfaceResult.extendResultByContext(validationContext);
            interfaceResults.add(interfaceResult);
        }
    }

    private ValidationContext<R> createValidationContextByAction(ValidationAction<R> validationAction, String name) {
        return new ValidationContext<>(validationAction.path(), validationAction.rootSource(), name, this.applicationContext);
    }

    protected List<ValidationAction<R>> buildEffectiveValidationActions(R source) {

        if(validationRules == null) {
            return Collections.emptyList();
        }

        List<ValidationAction<R>> resultList = new ArrayList<>();

        for(ValidationRule<R> rule : validationRules) {
            resultList.addAll(buildActionsByRule(rule, source));
        }

        return resultList;
    }

    private List<ValidationAction<R>> buildActionsByRule(ValidationRule<R> rule, R source) {
        String[] fragments = Optional.ofNullable(rule.getPath()).map(p -> p.split("\\.")).orElseGet(() -> new String[0]);
        List<PathElement> pathElements = new ArrayList<>();

        List<String> currentSubElements = new ArrayList<>();
        PathElement preElement = null;
        for(String fragment : fragments) {
            currentSubElements.add(fragment);
            boolean isListField = fragment.endsWith("[forEach]");
            boolean isOptionalField = fragment.contains("?");
            PathElement pathElement = new PathElement(fragment, String.join(".", currentSubElements), isListField, isOptionalField, preElement);
            pathElements.add(pathElement);
            preElement = pathElement;
        }

        return buildActionsByPathElements(pathElements, source, "", rule, source);
    }

    @SuppressWarnings("java:S3776")
    private List<ValidationAction<R>> buildActionsByPathElements(List<PathElement> pathElements, Object source, String basePath, ValidationRule<R> rule, R rootSource) {
        final List<ValidationAction<R>> result = new ArrayList<>();
        final List<String> cleanPathFragments = new ArrayList<>();
        final List<PathElement> remainingPathElements = new ArrayList<>(pathElements);

        Object target = source;
        for(PathElement pathElement : pathElements) {
            remainingPathElements.remove(pathElement);

            if(target == null) {
                if(pathElement.isOptionalField())
                    break;

                throw new InternalServerException(String.format("Cannot resolve '%s' on null", pathElement.getRawFieldName()));
            } else {
                target = computeTargetByPathElement(pathElement, target);

                if(target != null && pathElement.isListField) {
                    result.addAll(buildValidationActionsForListField(basePath, rule, rootSource, cleanPathFragments, remainingPathElements, target, pathElement));
                    return result;
                } else {
                    cleanPathFragments.add(pathElement.getRawFieldName());
                }

                if(target == null && pathElement.isOptionalField()) {
                    return result;
                }
            }
        }

        result.add(new ValidationAction<>(rootSource, buildCleanPath(basePath, cleanPathFragments), target, rule.getValidation()));

        return result;
    }

    private List<ValidationAction<R>> buildValidationActionsForListField(String basePath, ValidationRule<R> rule, R rootSource, List<String> cleanPathFragments, List<PathElement> remainingPathElements, Object target, PathElement pathElement) {
        final List<ValidationAction<R>> resultList = new ArrayList<>();
        final List<?> targetAsList = (List<?>) target;
        for(int i=0; i<targetAsList.size(); i++) {
            Object targetListElement = targetAsList.get(i);
            final String newBasePath = buildNewBasePath(basePath, pathElement, cleanPathFragments, i);
            resultList.addAll(buildActionsByPathElements(remainingPathElements, targetListElement,  newBasePath, rule, rootSource));
        }

        return resultList;
    }

    private String buildNewBasePath(String currentBasePath, PathElement pathElement, List<String> cleanPathFragments, int index) {
        final String cleanPath = buildCleanPath(currentBasePath, cleanPathFragments);
        return StringUtils.isNullOrEmpty(cleanPath) ? pathElement.getRawFieldName() + String.format("[%s]", index) : cleanPath + "." + pathElement.getRawFieldName() + String.format("[%s]", index);
    }

    private String buildCleanPath(String basePath, List<String> cleanPathFragments) {
        final String cleanPath;
        if(StringUtils.isNullOrEmpty(basePath)) {
            cleanPath = String.join(".", cleanPathFragments);
        } else if(cleanPathFragments.isEmpty()) {
            cleanPath = basePath;
        }else {
            cleanPath = basePath + "." + String.join(".", cleanPathFragments);
        }

        return cleanPath;
    }

    @SneakyThrows
    private Object computeTargetByPathElement(PathElement pathElement, Object source) {
        Class<?> type = source.getClass();
        Field field = ReflectionUtils.getFieldByName(type, pathElement.getRawFieldName());
        Method getter = ReflectionUtils.findGetterByField(field, type)
                .orElseThrow(() -> new ReflectionException("No getter present"));

        return getter.invoke(source);
    }

    private List<ValidationError<?>> buildValidationErrors(List<ValidationActionResult<R>> validationActionResults) {
        final List<ValidationError<?>> resultList = new ArrayList<>();
        for (ValidationActionResult<R> validationActionResult : validationActionResults) {
            for(ValidationInterfaceResult<R> validationInterfaceResult : validationActionResult.getInterfaceResults()) {
                resultList.addAll(validationInterfaceResult.getValidationErrors());
            }
        }
        return resultList;
    }

    private record PathElement(String fragment, String fullSubPath, boolean isListField, boolean isOptionalField,
                                   Validator.PathElement preElement) {

        public String getRawFieldName() {
                return fragment.replace("[forEach]", "")
                        .replace("?", "");
            }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PathElement that = (PathElement) o;

            return fullSubPath.equals(that.fullSubPath);
        }

        @Override
        public int hashCode() {
            return fullSubPath.hashCode();
        }
    }


}
