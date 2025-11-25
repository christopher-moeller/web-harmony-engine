package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.model.RegistryItemDto;
import com.webharmony.core.data.jpa.model.AppRegistryItem;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class RegistryItemService extends AbstractEntityCrudService<RegistryItemDto, AppRegistryItem> {

    @Override
    protected void configureMapping(MappingConfiguration<RegistryItemDto, AppRegistryItem> mappingConfiguration) {

        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
           dto.setValue(entity.getValue());
           getDefinedSelectableOptionsForItem(entity).ifPresent(dto::setDefinedSelectableOptions);
           dto.setJavaSpecificationType(getJavaSpecificationType(entity));
           return dto;
        });

        mappingConfiguration.withExtendedToEntityMapper((dto, entity, context) -> {
            entity.setValue(dto.getValue());
            return entity;
        });
    }

    private String getJavaSpecificationType(AppRegistryItem entity) {
        Class<?> type = entity.getJavaTypeClass();
        if(type.isEnum())
            return Enum.class.getName();

        return type.getName();
    }

    private Optional<List<Object>> getDefinedSelectableOptionsForItem(AppRegistryItem entity) {
        Class<?> javaType = entity.getJavaTypeClass();
        if(javaType.isEnum()) {
            return Optional.of(Arrays.asList(javaType.getEnumConstants()));
        } else {
            return Optional.empty();
        }
    }

}
