import {useI18nStore} from "~/store/i18nStore";
import {useDevStore} from "~/store/devStore";

export interface I18nFrontendTranslationEntry {
    classId: string,
    keyId: string,
    value: string
}

export interface I18nFrontendTranslation {
    language: string,
    translationEntries: I18nFrontendTranslationEntry[]
}

export default class I18N {

    static translation: I18nFrontendTranslation | undefined = undefined;

    classId: string;
    englishVersion: string | undefined;
    keyId: string | undefined;
    placeholders: {key: string, value: any}[];

    private constructor(classId: string, englishVersion: string |undefined) {
        this.classId = classId;
        this.englishVersion = englishVersion;
        this.keyId = englishVersion?.replace(" ", "")
        this.placeholders = []
    }

    public static translate(classId: string, englishVersion: string): I18N {
        return new I18N(classId, englishVersion);
    }

    public static staticTranslateRouterPagesJsonLabel(englishVersion: string): I18N {
        const instance = I18N.translate("project-configuration.json", englishVersion)
        instance.keyId = englishVersion.replaceAll(" ", "_").toLowerCase()
        return instance
    }

    public static of(classId: string) {
        return new I18N(classId, undefined);
    }

    public translate(englishVersion: string): I18N {
        this.englishVersion = englishVersion;
        this.keyId = englishVersion.replaceAll(" ", "_").toLowerCase()
        return this;
    }

    public add(key: string, value: any): I18N {
        this.placeholders.push({key, value})
        return this;
    }

    public build(): string {

        const i18nStore = useI18nStore()

        const entry = i18nStore.getFrontendTranslation?.translationEntries?.find(e => e.classId === this.classId && e.keyId == this.keyId)

        let resolvedTranslation = entry ? entry.value! : (this.englishVersion ? this.englishVersion : "")
        this.placeholders.forEach(p => {
            resolvedTranslation = resolvedTranslation.replace('{'+p.key+'}', p.value)
        })

        const suffix = useDevStore().getShowI18nKeys ? ` (ClassID: ${this.classId}, KeyID: ${this.keyId})` : ""

        return resolvedTranslation + suffix
    }
}