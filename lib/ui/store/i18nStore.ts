import {defineStore} from "pinia";
import {I18nFrontendTranslation, LanguageInfo} from "~/CoreApi";

interface State {
    frontendTranslation?: I18nFrontendTranslation,
    languageInfo?: LanguageInfo
}

export const useI18nStore = defineStore('i18nStore', {
    state: (): State => ({ frontendTranslation: undefined, languageInfo: undefined }),
    getters: {
        getFrontendTranslation: (state): I18nFrontendTranslation | undefined => state.frontendTranslation,
        getLanguageInfo: (state): LanguageInfo | undefined => state.languageInfo
    },
    actions: {
        setFrontendTranslations(frontendTranslation: I18nFrontendTranslation) {
            this.$state.frontendTranslation = frontendTranslation
        },
        setLanguageInfo(languageInfo: LanguageInfo) {
            this.$state.languageInfo = languageInfo
        }
    },
})