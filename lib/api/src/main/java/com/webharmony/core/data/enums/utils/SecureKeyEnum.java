package com.webharmony.core.data.enums.utils;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.i18n.ECoreI18nStaticTranslations;
import com.webharmony.core.service.SecureKeyService;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.InternalServerException;

public interface SecureKeyEnum {

    String name();

    default String getNotEmptyKey() {
        final String key = getKey();
        Assert.isNotEmpty(key).withException(() -> new InternalServerException(ECoreI18nStaticTranslations.SECURE_KEY_CANNOT_BE_EMPTY.getI18n().build()))
                .verify();

        return key;
    }

    default String getKey() {
        return ContextHolder.getContext().getBean(SecureKeyService.class)
                .getSecureKeyByName(name())
                .getKey();
    }
}
