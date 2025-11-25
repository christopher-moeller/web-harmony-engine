package com.webharmony.core.service.data.validation;

import com.webharmony.core.api.rest.model.view.ValidationRuleConfigurationModel;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.Validation;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationRule;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ValidatorService {

    private final ApplicationContext applicationContext;

    private final Map<Class<?>, Validator<?>> validatorMap = new HashMap<>();

    private final Map<NamedValidationInterface<?, ?>, NamedValidationInterface.NamedValidationInterfaceConfiguration> validationInterfaceConfigurations = new HashMap<>();

    public ValidatorService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public <R> void validate(R root) {
        final Validator<R> validator = findValidator(root);
        validator.validate(root);
        log.info("Executed {} validation rules for root class '{}'", validator.getValidationRules().size(), validator.getRootClass().getName());
    }

    @SuppressWarnings("unchecked")
    private <R> Validator<R> findValidator(R root) {
        return (Validator<R>) getValidatorForClass(root.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T> Validator<T> getValidatorForClass(Class<T> rootClass) {
        if(!validatorMap.containsKey(rootClass)) {
            precomputeValidatorForClass(rootClass);
        }
        return (Validator<T>) validatorMap.get(rootClass);
    }

    public void precomputeValidatorForClass(Class<?> rootClass) {
        validatorMap.put(rootClass, createValidatorForClass(rootClass));
    }

    private <R> Validator<R> createValidatorForClass(Class<R> rootClass) {
        return Optional.ofNullable(AnnotationUtils.findAnnotation(rootClass, Validation.class))
                .map(anno -> createValidatorForClassByConfigBuilder(rootClass, anno.value()))
                .orElseGet(() -> createDefaultValidatorForClass(rootClass));
    }

    private <R> Validator<R> createValidatorForClassByConfigBuilder(Class<R> rootClass, Class<? extends ValidationConfigBuilder<?>> validationConfigBuilderClass) {
        ValidationConfigBuilder<R> configBuilder = getValidationConfigBuilderInstanceByClass(validationConfigBuilderClass);
        List<ValidationRule<R>> rules = configBuilder.createNewBuilder().getValidationBuildingContext().getRootContext().buildValidationRules();
        Validator<R> validator = new Validator<>(rootClass, applicationContext);
        validator.setValidationRules(rules);
        return validator;
    }

    @SuppressWarnings("unchecked")
    private <R> ValidationConfigBuilder<R> getValidationConfigBuilderInstanceByClass(Class<? extends ValidationConfigBuilder<?>> validationConfigBuilderClass) {
        try {
            return (ValidationConfigBuilder<R>) applicationContext.getBean(validationConfigBuilderClass);
        } catch (BeansException e) {
            return (ValidationConfigBuilder<R>) ReflectionUtils.createNewInstanceWithEmptyConstructor(validationConfigBuilderClass);
        }
    }

    private <R> Validator<R> createDefaultValidatorForClass(Class<R> rootClass) {
        return new Validator<>(rootClass, applicationContext);
    }

    @SneakyThrows
    public ValidationRuleConfigurationModel loadConfigurationModel(String rootType, String path, String validationRuleName) {
        NamedValidationInterface<?, ?> namedValidationInterface = getValidationInterface(rootType, path, validationRuleName);

        ValidationRuleConfigurationModel model = new ValidationRuleConfigurationModel();
        model.setRootType(rootType);
        model.setPath(path);
        model.setValidationRuleName(namedValidationInterface.getName());

        var config = getValidationInterfaceConfig(namedValidationInterface);

        model.setIsActive(config.getIsActive());
        model.setAlwaysReturnInvalid(config.getAlwaysReturnInvalid());

        return model;
    }

    public NamedValidationInterface.NamedValidationInterfaceConfiguration getValidationInterfaceConfig(NamedValidationInterface<?, ?> namedValidationInterface) {
        return validationInterfaceConfigurations.getOrDefault(namedValidationInterface, new NamedValidationInterface.NamedValidationInterfaceConfiguration());
    }

    @SneakyThrows
    public void saveConfigurationModel(String rootType, String path, String validationRuleName, ValidationRuleConfigurationModel configModel) {

        NamedValidationInterface<?, ?> namedValidationInterface = getValidationInterface(rootType, path, validationRuleName);

        validationInterfaceConfigurations.putIfAbsent(namedValidationInterface, new NamedValidationInterface.NamedValidationInterfaceConfiguration());
        var config = validationInterfaceConfigurations.get(namedValidationInterface);

        if(configModel != null) {
            Optional.ofNullable(configModel.getAlwaysReturnInvalid()).ifPresent(config::setAlwaysReturnInvalid);
            Optional.ofNullable(configModel.getIsActive()).ifPresent(config::setIsActive);
        }

        loadConfigurationModel(rootType, path, validationRuleName);
    }

    @SneakyThrows
    private NamedValidationInterface<?, ?> getValidationInterface(String rootType, String path, String validationRuleName) {
        Validator<?> validator = this.getValidatorForClass(Class.forName(rootType));
        ValidationRule<?> validationRule = validator.getValidationRules().stream()
                .filter(v -> Objects.equals(v.getPath(), path))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Validation rule for path '%s' not found", path)));

        return validationRule.getValidation()
                .stream()
                .filter(i -> i.getName().equals(validationRuleName))
                .findAny()
                .orElseThrow(() -> new NotFoundException(String.format("Validation Interface with name '%s' not found", validationRuleName)));
    }

    public void reloadAllValidators() {
        resetCustomValidationConfig();
        validatorMap.keySet().forEach(this::precomputeValidatorForClass);
    }

    public void resetCustomValidationConfig() {
        validationInterfaceConfigurations.clear();
    }
}
