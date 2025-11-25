package com.webharmony.core.api.rest.validation;

import com.querydsl.jpa.impl.JPAQuery;
import com.webharmony.core.api.rest.model.utils.apiobject.ApiResource;
import com.webharmony.core.api.rest.model.webcontent.WebContentDto;
import com.webharmony.core.data.jpa.model.webcontent.AppWebContentArea;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContent;
import com.webharmony.core.data.jpa.model.webcontent.QAppWebContentArea;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotNullFieldValidator;
import com.webharmony.core.service.data.validation.utils.NamedValidationInterface;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WebContentDtoValidator implements ValidationConfigBuilder<WebContentDto>, I18nTranslation {

    private final I18N i18N = createI18nInstance(WebContentDtoValidator.class);

    private static final QAppWebContentArea qAppWebContentArea = QAppWebContentArea.appWebContentArea;
    private static final QAppWebContent qAppWebContent = QAppWebContent.appWebContent;

    @PersistenceContext
    private EntityManager em;

    @Override
    @SuppressWarnings("rawtypes")
    public void configureValidationBuilder(ValidationBuilder<WebContentDto, WebContentDto, ?, ? extends ValidationBuilder<WebContentDto, ?, ?, ?>> builder) {
        builder.ofField(WebContentDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(WebContentDto::getContent).withValidation(new NotNullFieldValidator<>())
                .ofField(WebContentDto::getArea).withValidation(new NotNullFieldValidator<>(), new AreaUniqueValidation());
    }


    private class AreaUniqueValidation<T extends ApiResource<?>> implements NamedValidationInterface<T, WebContentDto>{
        @Override
        public String getName() {
            return "AREA_UNIQUE_VALIDATION";
        }

        @Override
        public void validate(T value, ValidationContext<WebContentDto> validationContext) {
            if(value == null)
                return;

            final UUID primaryKeyAsUUID = value.getPrimaryKeyAsUUID();

            final AppWebContentArea appWebContentArea = new JPAQuery<>(em)
                    .select(qAppWebContentArea)
                    .from(qAppWebContentArea)
                    .where(qAppWebContentArea.uuid.eq(primaryKeyAsUUID))
                    .fetchOne();

            if(appWebContentArea == null) {
                validationContext.addValidationError(i18N.translate("Content area not found").build());
                return;
            }

            boolean isUniqueForContent = Optional.ofNullable(appWebContentArea.getOnlyOneContentInstanceAllowed())
                    .orElse(false);

            if(!isUniqueForContent)
                return;


            JPAQuery<Long> query = new JPAQuery<>(em)
                    .select(qAppWebContent.count())
                    .from(qAppWebContent)
                    .where(qAppWebContent.area.eq(appWebContentArea));


            final WebContentDto webContentDto = validationContext.getRootSource();
            Optional.ofNullable(webContentDto.getId())
                    .map(UUID::fromString).ifPresent(webContentUUID -> query.where(qAppWebContent.uuid.ne(webContentUUID)));

            if(Optional.ofNullable(query.fetchOne()).orElse(0L) > 0) {
                validationContext.addValidationError(i18N.translate("Content area is already used").build());
            }


        }
    }
}
