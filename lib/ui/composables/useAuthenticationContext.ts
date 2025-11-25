import {AccessRuleConfigJson, AuthenticatedUserDto, ECoreActorRight, ECoreRouterPage, PageJson} from "~/CoreApi";
import {useAuthenticationStore} from "~/store/authenticationStore";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";
import {CookieNames, CookieResolver} from "~/utils/HarmonyTypes";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

export interface AuthenticationContext {
    logoutAndClear(): Promise<void>,
    getIsAuthenticated(): Promise<boolean>,
    getUser(): Promise<AuthenticatedUserDto>,
    hasRight(rightId: string): Promise<boolean>,
    isAuthorizedForPage(page: PageJson): Promise<boolean>,
    isAuthorizedByAccessRuleConfig(accessRuleConfig?: AccessRuleConfigJson): Promise<boolean>,
    setToken(token: string | null): void,
    getToken(): string | null | undefined
}

async function getIsAuthenticatedInternal(config: AuthenticationConfig):Promise<boolean> {
    const token = config.cookieResolver.getCookieValue(CookieNames.AUTHENTICATION)
    if(!token) {
        return false
    }
    // for performance reasons we are not checking the state in the API
    return true
}

async function ensureUserIsAuthenticatedInternal(config: AuthenticationConfig) {
    const isAuthenticated = await getIsAuthenticatedInternal(config)
    if(!isAuthenticated) {
        throw new Error("User is not authenticated")
    }
}

async function getUserInternal(config: AuthenticationConfig): Promise<AuthenticatedUserDto> {
    await ensureUserIsAuthenticatedInternal(config)
    const store = useAuthenticationStore()
    const coreApi = useCoreApi(config.cookieResolver)
    const currentTokenValue = config.cookieResolver.getCookieValue(CookieNames.AUTHENTICATION)
    const cachedUser:AuthenticatedUserDto | undefined = store.getUser(currentTokenValue!)
    if(!cachedUser) {
        const loadedUserResponse = await coreApi.api().getAuthenticationApi().getOwnUser()
        if(loadedUserResponse.status === 200) {
            store.setUser(currentTokenValue, loadedUserResponse.data)
            return store.getUser(currentTokenValue!)!
        } else {
            config.cookieResolver.setCookieValue(CookieNames.AUTHENTICATION, null)
            await useRouterUtils().hardNavigateToPage(ECoreRouterPage.INDEX)
            throw new Error("Loading User: Forbidden")
        }
    } else {
        return cachedUser;
    }
}

async function hasRightInternal(rightId: string, config: AuthenticationConfig) {
    await ensureUserIsAuthenticatedInternal(config)
    const user:AuthenticatedUserDto = await getUserInternal(config)
    return user.effectiveRights ? user.effectiveRights.includes(rightId) : false;
}

async function isAuthorizedByAccessRuleConfigInternal(config: AuthenticationConfig, accessRuleConfig?: AccessRuleConfigJson): Promise<boolean> {
    const rightsForRule:String[] = accessRuleConfig?.rights ? accessRuleConfig.rights : [];
    if(accessRuleConfig?.isAndConnected) {
        for (let rightOfRule of rightsForRule) {
            const hasRight = await hasRightInternal(rightOfRule+"", config)
            if(!hasRight) {
                return false
            }
        }
        return true
    } else {
        for (let rightOfRule of rightsForRule) {
            const hasRight = await hasRightInternal(rightOfRule+"", config)
            if(hasRight) {
                return true
            }
        }
        return false
    }
}

async function isAuthorizedForPageInternal(page: PageJson, config: AuthenticationConfig): Promise<boolean> {
    if(page.path?.startsWith("/app") && !await hasRightInternal(ECoreActorRight.CORE_APP_ACCESS, config)) {
        return false
    }
    return await isAuthorizedByAccessRuleConfigInternal(config, page.accessRuleConfigJson)
}

interface AuthenticationConfig {
    cookieResolver: CookieResolver
}

export default function (cookieResolver?: CookieResolver):AuthenticationContext {
    const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    const config:AuthenticationConfig = {
        cookieResolver: internalCookieResolver
    }
    return {
        logoutAndClear: async () => {
            const tokenToLogout = config.cookieResolver.getCookieValue(CookieNames.AUTHENTICATION)
            const coreApi = useCoreApi(config.cookieResolver)
            config.cookieResolver.setCookieValue(CookieNames.AUTHENTICATION, null)
            if(tokenToLogout) {
                await coreApi.api().getAuthenticationApi().userLogout({token: tokenToLogout!})
            }
        },
        getIsAuthenticated: async () => await getIsAuthenticatedInternal(config),
        getUser: async () => {
            return getUserInternal(config)
        },
        hasRight: async (rightId: string) => {
            return await hasRightInternal(rightId, config)
        },
        isAuthorizedForPage: async (page: PageJson): Promise<boolean> => {
            return await isAuthorizedForPageInternal(page, config)
        },
        isAuthorizedByAccessRuleConfig: async (accessRuleConfig?: AccessRuleConfigJson): Promise<boolean> => {
            return await isAuthorizedByAccessRuleConfigInternal(config, accessRuleConfig)
        },
        setToken(token: string | null): void {
            config.cookieResolver.setCookieValue(CookieNames.AUTHENTICATION, token)
        },
        getToken(): string | null | undefined {
            return config.cookieResolver.getCookieValue(CookieNames.AUTHENTICATION)
        }
    }
}

