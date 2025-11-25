package com.webharmony.core.service.data.validation.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValidationActionResult<R> {
    private R rootSource;
    private List<ValidationInterfaceResult<R>> interfaceResults;
}
