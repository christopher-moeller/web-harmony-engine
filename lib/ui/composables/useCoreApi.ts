import coreApi, {ApiResolver, RestRequestParams} from "~/CoreApi";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import {CookieResolver} from "~/utils/HarmonyTypes";
import useHarmonyCookies from "~/composables/useHarmonyCookies";


export default function (cookieResolver?: CookieResolver) {
    const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    const harmonyFetch = useHarmonyFetch(internalCookieResolver)
    const apiResolver:ApiResolver = {
        resolveRequest(apiMethod: string, apiPath: string, body?: any): Promise<any> {
            return harmonyFetch.fetch(apiPath, {
                method: apiMethod,
                body: body
            })
        },
        resolveUrlForRestRequestParams(baseUrl: string, restRequestParams: RestRequestParams): string {
            return baseUrl + restRequestParams.toQueryString();
        }
    }
    return coreApi(apiResolver)
}

