package com.webharmony.core.service;

import com.webharmony.core.api.rest.controller.SelectableOptionContainerController;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractUnPagedOptionsContainer;
import com.webharmony.core.context.AppContext;
import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.utils.exceptions.BadRequestException;
import com.webharmony.core.utils.exceptions.InternalServerException;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ApiLink;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Optional;

@Getter
@Service
public class SelectableOptionContainerService {

    private List<? extends AbstractSelectableOptionsContainer<?>> optionContainers;

    public void initOptionContainers(AppContext appContext) {

        @SuppressWarnings("unchecked")
        List<Class<AbstractSelectableOptionsContainer<?>>> allContainerClasses = ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractSelectableOptionsContainer.class)
                .stream()
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> (Class<AbstractSelectableOptionsContainer<?>>) c)
                .toList();

        this.optionContainers = allContainerClasses
                .stream()
                .map(clazz -> getOrCreateContainerInstance(appContext, clazz))
                .toList();

        for(AbstractSelectableOptionsContainer<?> optionsContainer : optionContainers) {
            AbstractSelectableOptionsContainer<?> duplicate = optionContainers.stream()
                    .filter(c -> c != optionsContainer && c.getName().equals(optionsContainer.getName()))
                    .findAny()
                    .orElse(null);

            if(duplicate != null) {
                throw new InternalServerException(String.format("More than one option container with same name found (%s <-> %s)", optionsContainer.getClass().getName(), duplicate.getClass().getName()));
            }
        }
    }

    public ApiLink buildApiLinkForOptions(Class<? extends AbstractSelectableOptionsContainer<?>> optionsContainerClass, boolean asFilterOptions) {
        AbstractSelectableOptionsContainer<?> container = getOptionsContainerByClass(optionsContainerClass);
        return ApiLink.of(SelectableOptionContainerController.class, controller -> controller.getOptionsByContainer(container.getName(), asFilterOptions));
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractSelectableOptionsContainer<T> getOptionsContainerByClass(Class<? extends AbstractSelectableOptionsContainer<?>> optionsContainerClass) {
        return optionContainers.stream()
                .filter(c -> c.getClass().equals(optionsContainerClass))
                .findAny()
                .map(c -> (AbstractSelectableOptionsContainer<T>) c)
                .orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public <T> AbstractSelectableOptionsContainer<T> getOptionsContainerByName(String name) {
        return optionContainers.stream()
                .filter(c -> c.getName().equals(name))
                .findAny()
                .map(c -> (AbstractSelectableOptionsContainer<T>) c)
                .orElseThrow(() -> new NotFoundException(String.format("OptionContainer with name '%s' does not exist", name)));
    }

    @SuppressWarnings("unchecked")
    public List<Object> getSelectableOptionsForContainerByName(String containerName, boolean asFilterOptions) {
        AbstractSelectableOptionsContainer<?> container = getOptionsContainerByName(containerName);
        ContextHolder.getContext().assertCurrentActorHasRight(container.getApplicationRight());

        if(container instanceof AbstractUnPagedOptionsContainer<?> unPagedOptionsContainer) {
            if(asFilterOptions) {
                return unPagedOptionsContainer.getSelectableOptionsAsFilterOptions()
                        .stream()
                        .map(e -> (Object) e)
                        .toList();
            } else {
                return (List<Object>) unPagedOptionsContainer.getSelectableOptions();
            }
        } else {
            throw new BadRequestException("Unpaged options not supported currenty");
        }
    }

    private AbstractSelectableOptionsContainer<?> getOrCreateContainerInstance(AppContext appContext, Class<? extends AbstractSelectableOptionsContainer<?>> clazz) {
        Optional<? extends AbstractSelectableOptionsContainer<?>> springBean = appContext.getBeanIfExists(clazz);
        return springBean.isPresent() ? springBean.get() : ReflectionUtils.createNewInstanceWithEmptyConstructor(clazz);
    }
}
