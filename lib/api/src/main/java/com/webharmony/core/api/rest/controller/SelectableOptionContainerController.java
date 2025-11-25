package com.webharmony.core.api.rest.controller;

import com.webharmony.core.api.rest.controller.utils.AbstractBaseController;
import com.webharmony.core.api.rest.controller.utils.ApiAuthentication;
import com.webharmony.core.api.rest.controller.utils.ApiAuthenticationType;
import com.webharmony.core.api.rest.model.utils.optioncontainers.utils.AbstractSelectableOptionsContainer;
import com.webharmony.core.service.SelectableOptionContainerService;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@ApiController("api/optionContainers")
public class SelectableOptionContainerController extends AbstractBaseController implements ApplicationContextAware {

    private final SelectableOptionContainerService selectableOptionContainerService;

    public SelectableOptionContainerController(SelectableOptionContainerService selectableOptionContainerService) {
        this.selectableOptionContainerService = selectableOptionContainerService;
    }

    @GetMapping
    @ApiAuthentication(ApiAuthenticationType.PUBLIC)
    public ResponseEntity<List<String>> getOptionContainers() {
        List<String> names = selectableOptionContainerService.getOptionContainers()
                .stream()
                .map(AbstractSelectableOptionsContainer::getName)
                .toList();

        return ResponseEntity.ok(names);
    }

    @GetMapping("{containerName}/options")
    public ResponseEntity<List<Object>> getOptionsByContainer(@PathVariable("containerName") String containerName, @RequestParam("isFilterOptions") Boolean isFilterOptions) {
        return ResponseEntity.ok(selectableOptionContainerService.getSelectableOptionsForContainerByName(containerName, isFilterOptions));
    }

    @Override
    @SneakyThrows
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        addSecuredMethod(SelectableOptionContainerController.class.getMethod("getOptionsByContainer", String.class, Boolean.class)); // secured in selectableOptionContainerService.getSelectableOptionsForContainerByName()
    }
}
