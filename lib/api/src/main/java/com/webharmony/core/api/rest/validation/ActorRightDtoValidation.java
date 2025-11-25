package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.ActorRightDto;
import com.webharmony.core.data.jpa.model.user.QAppActorRight;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.UniqueResourceI18nFieldValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class ActorRightDtoValidation implements ValidationConfigBuilder<ActorRightDto> {

    private static final QAppActorRight qAppActorRight = QAppActorRight.appActorRight;

    @Override
    public void configureValidationBuilder(ValidationBuilder<ActorRightDto, ActorRightDto, ?, ? extends ValidationBuilder<ActorRightDto, ?, ?, ?>> builder) {
        builder.ofField(ActorRightDto::getLabel).withValidation(new NotEmptyTextFieldValidator<>())
                .ofField(ActorRightDto::getLabel).withValidation(new UniqueResourceI18nFieldValidator<>(qAppActorRight, q -> q.uuid, q -> q.label))
                .ofField(ActorRightDto::getDescription).withValidation(new NotEmptyTextFieldValidator<>());
    }
}
