
import {CookieNames, CookieResolver, HarmonyAxiosResponse} from "~/utils/HarmonyTypes";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {useI18nStore} from "~/store/i18nStore";
import {DateUtils} from "~/utils/DateUtils";
import {LanguageInfo} from "~/CoreApi";

export interface LanguageUtilsContext {
    formatDateString(dateValue: string): Promise<string>,
    initLanguageInfoIfNotExists(): Promise<void>,
    formatLanguageByLocalState(dateValue: string): string
}

async function getOrLoadLanguageInfo(cookieResolver: CookieResolver): Promise<LanguageInfo> {
    const i18nStore = useI18nStore()
    const languageInfo = i18nStore.getLanguageInfo
    if(languageInfo) {
        return languageInfo;
    } else {
        const cookieValue = cookieResolver.getCookieValue(CookieNames.LANGUAGE)
        const defaultLanguageResponse = await  useCoreApi().api().getServerInfoApi().getDefaultLanguage()
        const languageKey:string = cookieValue ? cookieValue : defaultLanguageResponse.data.id+""
        const apiResponse = await useCoreApi().api().getServerInfoApi().getLanguageById(languageKey);
        const loadedLanguageInfo = apiResponse.data!
        i18nStore.setLanguageInfo(loadedLanguageInfo)
        return loadedLanguageInfo
    }
}

function formatDateByLanguageInfo(dateValue: string, languageInfo:LanguageInfo): string {
    const dateTemplate = languageInfo.dateFormatTemplate!+""
    return DateUtils.formatDateString(dateValue, dateTemplate)
}

async function formatDateStringInternal(dateValue: string, cookieResolver: CookieResolver): Promise<string> {
    const languageInfo = await getOrLoadLanguageInfo(cookieResolver)
    return formatDateByLanguageInfo(dateValue, languageInfo)
}

export default function (cookieResolver?: CookieResolver):LanguageUtilsContext {
    const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    return {
        async formatDateString(dateValue: string): Promise<string> {
            return formatDateStringInternal(dateValue, internalCookieResolver)
        },
        async initLanguageInfoIfNotExists(): Promise<void> {
            await getOrLoadLanguageInfo(internalCookieResolver)
        },
        formatLanguageByLocalState(dateValue: string): string {
            const i18nStore = useI18nStore()
            const languageInfo = i18nStore.getLanguageInfo
            if(!languageInfo) {
                throw new Error("Language Info is not available: please call initLanguageInfoIfNotExists() first")
            }
            return formatDateByLanguageInfo(dateValue, languageInfo)
        }
    }
}