package com.webharmony.core.api.rest.model;

import com.webharmony.core.api.rest.model.utils.AbstractResourceDto;
import com.webharmony.core.api.rest.model.utils.annotations.ReadOnlyAttribute;
import com.webharmony.core.api.rest.validation.SecureKeyValidation;
import com.webharmony.core.service.data.validation.utils.Validation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Validation(SecureKeyValidation.class)
@AllArgsConstructor
@NoArgsConstructor
public class SecureKeyDto extends AbstractResourceDto {

    @ReadOnlyAttribute
    private String name;
    private String key;

}
