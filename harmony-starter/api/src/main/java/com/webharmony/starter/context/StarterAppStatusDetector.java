package com.webharmony.starter.context;

import com.webharmony.core.context.AppStatusDetector;
import com.webharmony.core.i18n.EI18nLanguage;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Primary
@Component
public class StarterAppStatusDetector extends AppStatusDetector {

    @Override
    public DetectionResult detect(EI18nLanguage systemLanguage) {
        return new DetectionResult(new ArrayList<>(), new ArrayList<>());
    }
}
