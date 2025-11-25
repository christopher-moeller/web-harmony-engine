package com.webharmony.core.api.rest.validation;

import com.webharmony.core.api.rest.model.ReportUserBugRequest;
import com.webharmony.core.service.data.validation.fieldvalidators.OverallFileWebDataValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.NotEmptyTextFieldValidator;
import com.webharmony.core.service.data.validation.fieldvalidators.SingleFileWebDataValidator;
import com.webharmony.core.service.data.validation.utils.ValidationBuilder;
import com.webharmony.core.service.data.validation.utils.ValidationConfigBuilder;

public class ReportedUserErrorRequestValidation implements ValidationConfigBuilder<ReportUserBugRequest> {

    @Override
    public void configureValidationBuilder(ValidationBuilder<ReportUserBugRequest, ReportUserBugRequest, ?, ? extends ValidationBuilder<ReportUserBugRequest, ?, ?, ?>> builder) {
        builder.ofField(ReportUserBugRequest::getDescription).withValidation(new NotEmptyTextFieldValidator<>());
        builder.ofField(ReportUserBugRequest::getAttachments).withValidation(new OverallFileWebDataValidator<>());
        builder.ofOptionalListField(ReportUserBugRequest::getAttachments).withValidation(new SingleFileWebDataValidator<>());

    }

}
