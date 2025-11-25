import {PageJson} from "~/CoreApi";
import {RouteLocationNormalizedLoaded, RouteParamsRaw, useRouter} from "vue-router";
import {Ref} from "vue";

export interface RouterUtils {
    getTitleOfCurrentPage(): string,
    reloadCurrentPage(): void,
    getResourceIdByUrl(): string,
    softNavigateToPage(pageId: string, params?: RouteParamsRaw): Promise<void>,
    hardNavigateToPage(pageId: string, params?: RouteParamsRaw): Promise<void>,
    goBack(): void,
    getCurrentPath(): string,
    softNavigateToPath(path: string): Promise<void>,
    hardNavigateToPath(path: string): Promise<void>,
    getCurrentRoute(): Ref<RouteLocationNormalizedLoaded>
}

export default function ():RouterUtils {
    const router = useRouter()
    return {
        getTitleOfCurrentPage(): string {
            const page:PageJson | undefined = <PageJson> router.currentRoute?.value?.meta?.page;
            return  page ? I18N.staticTranslateRouterPagesJsonLabel(page.englishLabel + "").build() : process.env.PROJECT_TITLE+""
        },
        reloadCurrentPage(): void {
            router.go(0)
        },
        getResourceIdByUrl(): string {
            return <string>router.currentRoute.value.params.id
        },
        async softNavigateToPage(pageId: string, params?: RouteParamsRaw): Promise<void> {
            await router.push({
                name: pageId,
                params
            })
        },
        async hardNavigateToPage(pageId: string, params?: RouteParamsRaw): Promise<void> {
            await router.push({
                name: pageId,
                params
            })
            router.go(0)
        },
        goBack(): void {
            router.back()
        },
        getCurrentPath(): string {
            return router.currentRoute.value.path
        },
        async softNavigateToPath(path: string): Promise<void> {
            await router.push(path)
        },
        getCurrentRoute(): Ref<RouteLocationNormalizedLoaded> {
            return router.currentRoute
        },
        async hardNavigateToPath(path: string): Promise<void> {
            await router.push(path)
            router.go(0)
        }
    }
}

