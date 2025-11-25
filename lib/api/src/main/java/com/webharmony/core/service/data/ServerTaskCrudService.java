package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.ServerTaskDto;
import com.webharmony.core.data.jpa.model.AppServerTask;
import com.webharmony.core.data.jpa.repository.ServerTaskRepository;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServerTaskCrudService extends AbstractEntityCrudService<ServerTaskDto, AppServerTask> implements I18nTranslation {

    private final I18N i18N = createI18nInstance(ServerTaskCrudService.class);

    private final ServerTaskRepository serverTaskRepository;

    public ServerTaskCrudService(ServerTaskRepository serverTaskRepository) {
        this.serverTaskRepository = serverTaskRepository;
    }

    public Optional<AppServerTask> findByTaskId(String taskId) {
        return serverTaskRepository.findByTaskId(taskId);
    }

    @Override
    protected void configureMapping(MappingConfiguration<ServerTaskDto, AppServerTask> mappingConfiguration) {
        mappingConfiguration.withToEntityMapper((dto, entity, context) -> {
            entity.setTaskName(dto.getTaskName());
           return entity;
        });

        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
           final String lastExecution = Optional.ofNullable(entity.getLastExecution())
                   .map(CustomLocalDateTimeSerializer::parseDateToString)
                   .orElse(i18N.translate("Not executed yet").build());

           dto.setLastExecution(lastExecution);
           return dto;
        });
    }
}
