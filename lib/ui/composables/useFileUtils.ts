
import {ECoreBackendPath} from "~/CoreApi";
import {CookieResolver, HarmonyAxiosResponse} from "~/utils/HarmonyTypes";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

export interface FileUtilsContext {
    createBlobFromBase64EncodedFile(base64Value: string): Blob,
    saveBlobInBrowserDownloads(blob: Blob, fileName: string): void,
    saveBlobInBrowserByHarmonyResponse(harmonyResponse: HarmonyAxiosResponse): void,
    downloadFileFromApi(url: string, queryParams?: any): Promise<void>
}

function createBlobFromBase64EncodedFileInternal(base64Value: string): Blob {
    const byteCharacters = atob(base64Value.split(",")[1]);
    const byteNumbers = [];

    for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers.push(byteCharacters.charCodeAt(i));
    }

    return new Blob([new Uint8Array(byteNumbers)])
}

function saveBlobInBrowserDownloadsInternal(blob: Blob, fileName: string) {
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', fileName)
    document.body.appendChild(link)
    link.click()
}

function saveBlobInBrowserByHarmonyResponseInternal(harmonyResponse: HarmonyAxiosResponse) {
    const fileName = harmonyResponse.axiosResponse.headers['content-disposition']
    saveBlobInBrowserDownloadsInternal(new Blob([harmonyResponse.axiosResponse.data]), fileName)
}

async function downloadFileFromApiInternal(cookieResolver: CookieResolver, url: string, queryParams?: any) {
    const response = await useHarmonyFetch(cookieResolver).fetch(ECoreBackendPath.API_I18NENTITYATTRIBUTE_EXPORT, {responseType: 'blob', method: 'GET', queryParams})
    saveBlobInBrowserByHarmonyResponseInternal(response)
}

export default function (cookieResolver?: CookieResolver):FileUtilsContext {
    const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    return {
        createBlobFromBase64EncodedFile(base64Value: string): Blob {
            return createBlobFromBase64EncodedFileInternal(base64Value);
        },
        saveBlobInBrowserDownloads(blob: Blob, fileName: string) {
            saveBlobInBrowserDownloadsInternal(blob, fileName)
        },
        saveBlobInBrowserByHarmonyResponse(harmonyResponse: HarmonyAxiosResponse) {
            saveBlobInBrowserByHarmonyResponseInternal(harmonyResponse)
        },
        async downloadFileFromApi(url: string, queryParams?: any) {
            await downloadFileFromApiInternal(internalCookieResolver, url, queryParams)
        }
    }
}