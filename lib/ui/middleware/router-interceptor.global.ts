import {defineNuxtRouteMiddleware, navigateTo} from "#app";
import {useRouterStore} from "@core/store/routerStore"
import {PageJson} from "~/CoreApi";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import useAuthenticationContext from "~/composables/useAuthenticationContext";

export default defineNuxtRouteMiddleware(async (to, from) => {

    if(to.path === "/pageNotFound")
        return

    const page:PageJson = <PageJson> to.meta.page

    if(!page) {
        if(to.path === from.path) {
            return navigateTo('/pageNotFound')
        } else {
            return navigateTo('/pageNotFound?redirectFrom=' + from.path)
        }
    }

    if(process.client) {
        const routerStore = useRouterStore()
        routerStore.setIsInRoutingMode(true)
    }

    if(to.path === "/forbidden")
        return

    const isPublicPage = isPublic(page)
    if(isPublicPage)
        return

    const authenticationContext = useAuthenticationContext(useHarmonyCookies())
    const isUserAuthenticated = await authenticationContext.getIsAuthenticated()
    if(!isUserAuthenticated) {
        return navigateTo('/login?redirectURL=' + to.path)
    }

    const userHasAccessToPage = await authenticationContext.isAuthorizedForPage(page)
    if(!userHasAccessToPage) {
        return navigateTo('/forbidden')
    }
})



function isPublic(page:PageJson): boolean {
    return !!page.accessRuleConfigJson?.isPublic
}


