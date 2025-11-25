package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.model.HarmonyEventDto;
import com.webharmony.core.data.jpa.model.harmonyevent.AppHarmonyEvent;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import org.springframework.stereotype.Service;

@Service
public class HarmonyEventCrudService extends AbstractEntityCrudService<HarmonyEventDto, AppHarmonyEvent> {

    @Override
    protected void configureMapping(MappingConfiguration<HarmonyEventDto, AppHarmonyEvent> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setCreatedBy(entity.getCreatedBy());
            return dto;
        });
    }
}
