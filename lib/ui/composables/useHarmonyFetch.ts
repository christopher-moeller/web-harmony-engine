import axios, {AxiosRequestConfig, AxiosResponse} from "axios";
import useBannerContext from "~/composables/useBannerContext";
import {
    CookieNames,
    CookieResolver,
    HarmonyAxiosConfig,
    HarmonyAxiosResponse,
    HarmonyRestError
} from "~/utils/HarmonyTypes";

const DEFAULT_FALLBACK_URL: string = "http://localhost:8080"

interface HarmonyFetchOptions {
    fetch(url: string, config?: HarmonyAxiosConfig): Promise<HarmonyAxiosResponse>
}

function getBaseUrl(): string | undefined {
    const hasProdApiUrl = !!process.env.PROD_API_URL;
    if(hasProdApiUrl) {
        if(process.server) {
            return process.env.PROD_API_URL
        } else {
            return undefined;
        }
    } else {
        if(process.server || process.env.PROFILE == "DEV") {
            return process.env.BASE_URL ? process.env.BASE_URL : DEFAULT_FALLBACK_URL
        } else {
            return undefined
        }
    }
}

function checkBaseResponseConditions(axiosResponse:AxiosResponse, axiosConfig:AxiosRequestConfig): void {

    if(!axiosResponse || axiosResponse.status == null) {
        const error:HarmonyRestError = {
            text: "Empty response when trying to fetch "+axiosConfig.method + " BASE_URL="+axiosConfig.baseURL+" URL="+axiosConfig.url+" SERVER_SIDE="+process.server+" Response="+JSON.stringify(axiosResponse)+" PROFILE="+process.env.PROFILE,
            axiosResponse: axiosResponse,
            axiosConfig: axiosConfig
        }
        console.error(error.text)
        throw error
    }

}

export default function (cookieResolver: CookieResolver): HarmonyFetchOptions {
    return {
        async fetch(url: string, config?: HarmonyAxiosConfig): Promise<HarmonyAxiosResponse> {
            const axiosConfig:AxiosRequestConfig = {
                url,
                method: config?.method || 'GET',
                data: config?.body,
                headers: config?.headers || {},
                params: config?.queryParams,
                responseType: config?.responseType,
                baseURL: getBaseUrl()
            }

            const tokenValue = cookieResolver.getCookieValue(CookieNames.AUTHENTICATION)
            if(tokenValue) {
                // @ts-ignore
                axiosConfig.headers['Authentication'] = tokenValue
            }

            const languageValue = cookieResolver.getCookieValue(CookieNames.LANGUAGE)
            if(languageValue) {
                // @ts-ignore
                axiosConfig.headers['language'] = languageValue
            }

            const useDefaultErrorHandler = config?.useDefaultErrorHandler !== undefined ? config.useDefaultErrorHandler : true


            try {
                let axiosResponse:AxiosResponse = await axios.request(axiosConfig)

                checkBaseResponseConditions(axiosResponse, axiosConfig)

                return {
                    data: axiosResponse.data,
                    status: axiosResponse.status,
                    axiosRequestConfig: axiosConfig,
                    success: axiosResponse.status === 200,
                    axiosResponse: axiosResponse
                }
            } catch (axiosError) {
                // @ts-ignore
                const axiosResponse: AxiosResponse = <AxiosResponse>axiosError.response
                checkBaseResponseConditions(axiosResponse, axiosConfig)

                const harmonyResponse: HarmonyAxiosResponse = {
                    data: axiosResponse.data,
                    status: axiosResponse.status,
                    axiosRequestConfig: axiosConfig,
                    success: false,
                    axiosResponse: axiosResponse
                }

                if (useDefaultErrorHandler) {
                    if (axiosResponse.status === 401) {
                        cookieResolver.setCookieValue(CookieNames.AUTHENTICATION, null)
                    } else {
                        if (process.client) {
                            useBannerContext().pushServerErrorBanner(harmonyResponse)
                        }
                    }
                }

                return harmonyResponse
            }
        }
    }
}

