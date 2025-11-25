package com.webharmony.core.service.appviewmodels;

import com.webharmony.core.api.rest.controller.AppViewModelController;
import com.webharmony.core.api.rest.model.utils.ComplexTypeSchema;
import com.webharmony.core.api.rest.model.utils.EJsonType;
import com.webharmony.core.api.rest.model.utils.ViewModel;
import com.webharmony.core.api.rest.model.utils.annotations.ViewModelLinks;
import com.webharmony.core.api.rest.model.utils.annotations.utils.ApiLinkSpecification;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.ValidatorService;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ViewModelService implements I18nTranslation {

    private final I18N i18N = createI18nInstance(ViewModelService.class);

    private Map<ViewModelInfoDto, Class<? extends ViewModel>> viewModelInfoDtos;

    private final ValidatorService validatorService;

    public ViewModelService(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    private Map<ViewModelInfoDto, Class<? extends ViewModel>> loadViewModelInfoDtos() {
        return ReflectionUtils.getAllProjectClassesImplementingSuperClass(ViewModel.class)
                .stream()
                .collect(Collectors.toMap(this::createInfoDtoByClass, c -> c));
    }

    private ViewModelInfoDto createInfoDtoByClass(Class<? extends ViewModel> viewModelClass) {

        ViewModelInfoDto dto = new ViewModelInfoDto();
        dto.setViewModelName(viewModelClass.getSimpleName());
        dto.setSchema(createComplexTypeSchemaByModelClass(viewModelClass));

        ViewModelLinks viewModelLinks = AnnotationUtils.findAnnotation(viewModelClass, ViewModelLinks.class);
        if(viewModelLinks != null) {
            Optional.ofNullable(viewModelLinks.loadLink())
                    .filter(type -> !type.equals(ApiLinkSpecification.class))
                    .map(ApiLinkSpecification::instanceOf)
                    .map(ApiLinkSpecification::getLink)
                    .ifPresentOrElse(dto::setLoadLink, () -> dto.setLoadLink(ApiLink.of(AppViewModelController.class, c -> c.getInitialViewModelTemplate(dto.getViewModelName()))));

            Optional.ofNullable(viewModelLinks.saveLink())
                    .map(ApiLinkSpecification::instanceOf)
                    .map(ApiLinkSpecification::getLink)
                    .ifPresent(dto::setSaveLink);

        }

        return dto;
    }

    private ComplexTypeSchema createComplexTypeSchemaByModelClass(Class<? extends ViewModel> viewModelClass) {
        ComplexTypeSchema schema = new ComplexTypeSchema();
        schema.setSimpleType(viewModelClass.getSimpleName());
        schema.setJavaType(viewModelClass.getName());
        schema.setJsonType(EJsonType.OBJECT.name());
        schema.setFields(ReflectionUtils.createFieldMapForType(viewModelClass));
        return schema;
    }

    public Set<ViewModelInfoDto> getAllViewModelInfos() {
        return viewModelInfoDtos.keySet();
    }

    public ViewModelInfoDto getViewModelByName(String viewModelName) {
        return viewModelInfoDtos.keySet()
                .stream()
                .filter(v -> v.getViewModelName().equals(viewModelName))
                .findAny()
                .orElseThrow(() -> new NotFoundException(i18N.translate("ViewModel with name '{viewModelName}' not found").add("viewModelName", viewModelName).build()));
    }

    public ViewModel createInitialViewModelTemplateByName(String viewModelName) {
        ViewModelInfoDto viewModelInfoDto = getViewModelByName(viewModelName);
        Class<? extends ViewModel> viewModelClass = viewModelInfoDtos.get(viewModelInfoDto);
        return ReflectionUtils.createNewInstanceWithEmptyConstructor(viewModelClass);
    }

    public void init() {
        this.viewModelInfoDtos = loadViewModelInfoDtos();
        this.viewModelInfoDtos.values().forEach(validatorService::precomputeValidatorForClass);
    }
}
