package com.webharmony.core.i18n;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.i18n.AppI18nEntityAttribute;
import com.webharmony.core.service.i18n.I18nKeyEntryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Slf4j
public class I18N {

    public static final EI18nLanguage CODING_LANGUAGE = EI18nLanguage.ENGLISH;


    private final Class<?> clazz;

    private I18N(Class<?> clazz) {
        this.clazz = clazz;
    }

    protected static I18N of(Class<?> clazz) {
        return new I18N(clazz);
    }

    public static AppI18nEntityAttribute entityAttribute(EI18nLanguage language, String value) {
        AppI18nEntityAttribute attribute = new AppI18nEntityAttribute();
        attribute.putAttributeValue(language, value);
        return attribute;
    }

    public TranslationBuilder translate(String englishVersion) {
        TranslationBuilder builder = new TranslationBuilder();
        builder.i18N = this;
        builder.defaultEnglishVersion = englishVersion;
        return builder;
    }

    private String getClassId() {
        return this.clazz.getName();
    }

    public static final class TranslationBuilder {
        private I18N i18N;
        private String defaultEnglishVersion;
        private final List<I18nPlaceholder> placeholders = new ArrayList<>();

        public TranslationBuilder add(String key, Object value) {
            this.placeholders.add(new I18nPlaceholder(key, value));
            return this;
        }

        public String build() {
            return build(null);
        }

        public String build(EI18nLanguage language) {
            final String classId = i18N.getClassId();
            final String keyId = getKeyId();
            final String unresolvedText = getUnresolvedTranslationText(classId, keyId, language);

            return resolvePlaceholders(unresolvedText);
        }

        private String getUnresolvedTranslationText(String classId, String keyId, EI18nLanguage language) {

            final ApplicationContext springContext = ContextHolder.getSpringContext();
            if(springContext == null) {
                log.info("Application Context is not set currently: Using default english version: '{}' for class '{}' and key '{}'", this.defaultEnglishVersion, classId, keyId);
                return this.defaultEnglishVersion;
            }

            return ContextHolder.getSpringContext().getBean(I18nKeyEntryService.class)
                    .getTranslation(i18N.getClassId(), getKeyId(), language)
                    .orElseGet(() -> {
                        log.error("No translation found for class '{}' and key '{}'. Using default english version: '{}'", classId, keyId, this.defaultEnglishVersion);
                        return this.defaultEnglishVersion;
                    });
        }

        private String resolvePlaceholders(String unresolvedText) {
            String builderText = unresolvedText;
            for (I18nPlaceholder placeholder : this.placeholders) {
                final String toReplace = "{" + placeholder.key() + "}";
                builderText = builderText.replace(toReplace, Objects.toString(placeholder.value()));
            }
            return builderText;
        }

        private String getKeyId() {
            return I18N.createKeyIdByEnglishDefaultValue(this.defaultEnglishVersion);
        }
    }

    public static String createKeyIdByEnglishDefaultValue(String englishDefaultValue) {
        return englishDefaultValue.replace(" ", "_")
                .replace("\n","")
                .replace("\\n","")
                .toLowerCase();
    }

}
