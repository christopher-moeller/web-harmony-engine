import {CookieNames, CookieResolver} from "~/utils/HarmonyTypes";
import {CookieRef} from "#app";

interface CookieEntry {
    name: string,
    instance: CookieRef<string | null | undefined>
}

function initCookieInstances(names: string[]): CookieEntry[] {
    const result:CookieEntry[] = [];
    for (let name of names) {
        const instance = useCookie(name)
        result.push({name, instance})
    }
    return result;
}

function findEntryByNameOrThrow(cookieName: string, instances:CookieEntry[]): CookieEntry {
    const entry = instances.find(e => e.name === cookieName)
    if(!entry) {
        throw new Error("No cookie with name '" + cookieName +"' initialized");
    }
    return entry
}

export default function ():CookieResolver {

    const cookieInstances:CookieEntry[] = initCookieInstances([CookieNames.AUTHENTICATION, CookieNames.LANGUAGE])

    return {
        getCookieValue(cookieName: string): string | null | undefined {
            return findEntryByNameOrThrow(cookieName, cookieInstances).instance.value
        },
        setCookieValue(cookieName: string, value: string | null | undefined) {
            findEntryByNameOrThrow(cookieName, cookieInstances).instance.value = value
        }
    }
}

