package com.webharmony.core.service.data;

import com.webharmony.core.api.rest.jackson.CustomLocalDateTimeSerializer;
import com.webharmony.core.api.rest.model.ApplicationExceptionDto;
import com.webharmony.core.api.rest.model.error.FrontendRuntimeError;
import com.webharmony.core.data.jpa.model.error.AppApplicationEx;
import com.webharmony.core.service.data.mapper.MappingConfiguration;
import com.webharmony.core.service.data.utils.AbstractEntityCrudService;
import com.webharmony.core.utils.exceptions.ExceptionHelper;
import com.webharmony.core.utils.exceptions.utils.EApplicationErrorLocation;
import com.webharmony.core.utils.log.LogWatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ApplicationExceptionCrudService extends AbstractEntityCrudService<ApplicationExceptionDto, AppApplicationEx> {


    @Override
    protected void configureMapping(MappingConfiguration<ApplicationExceptionDto, AppApplicationEx> mappingConfiguration) {
        mappingConfiguration.withExtendedToDtoMapper((entity, dto, context) -> {
            dto.setTimestamp(Optional.ofNullable(entity.getCreatedAt()).map(CustomLocalDateTimeSerializer::parseDateToString).orElse("-"));
            return dto;
        });
    }

    @Transactional
    public void persistApplicationExceptionByThrowable(Throwable throwable) {
        AppApplicationEx entity = new AppApplicationEx();
        entity.setExceptionType(throwable.getClass().toString());
        entity.setMessage(getStringForEntityMessage(throwable));
        entity.setCodeLocation(EApplicationErrorLocation.BACKEND);
        entity.setStacktrace(ExceptionHelper.buildStacktraceText(throwable));
        entity.setLog(String.join("\n", LogWatcher.getInstance().getLastLogEntries(20)));

        saveEntity(entity);
    }

    private String getStringForEntityMessage(Throwable throwable) {
        return ExceptionHelper.getThrowableMessage(throwable)
                .map(message -> message.length() > 255 ? message.substring(0, 255) : message)
                .orElse(null);
    }

    @Transactional
    public void persistApplicationExceptionByFrontendException(FrontendRuntimeError frontendException) {
        AppApplicationEx entity = new AppApplicationEx();
        entity.setExceptionType("JS Error");
        entity.setMessage(frontendException.getMessage());
        entity.setCodeLocation(EApplicationErrorLocation.FRONTEND);

        final String stacktrace = String.format("UrL: %s, Line: %s, Col: %s%nError:%n%s", frontendException.getUrl(), frontendException.getLine(), frontendException.getCol(), frontendException.getError());
        entity.setStacktrace(stacktrace);

        saveEntity(entity);
    }
}
