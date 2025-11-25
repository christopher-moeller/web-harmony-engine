package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.CronJobDto;
import com.webharmony.core.data.jpa.model.QAppCronJob;
import com.webharmony.core.service.data.validation.fieldvalidators.CronTriggerFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.LimitTextLengthValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UniqueResourceI18nFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class CronJobDtoValidation implements ValidationConfigBuilder<CronJobDto> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<CronJobDto, CronJobDto, ?, ? extends ValidationBuilder<CronJobDto, ?, ?, ?>> builder) {
        builder.ofField(CronJobDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>(), new LimitTextLengthValidator<>(), new UniqueResourceI18nFieldValidator<>(QAppCronJob.appCronJob, c -> c.uuid, c -> c.label));
        builder.ofField(CronJobDto::getDescription).withValidation(new NotEmptyTextFieldValidator<>());
        builder.ofField(CronJobDto::getCronTrigger).withValidation(new NotEmptyTextFieldValidator<>(), new CronTriggerFieldValidator<>());
    }
}
