import {Ref} from "vue";
import {Banner, BannerType, useBannerStore} from "~/store/bannerStore";
import {UUIDUtils} from "~/utils/UUIDUtils";
import {HarmonyAxiosResponse, HarmonyErrorBannerData, HarmonyErrorBannerType} from "~/utils/HarmonyTypes";

const i18n = I18N.of("useBannerContext")

export interface BannerContext {
    getActiveBanners(): Ref<Banner[]>,
    pushBanner(banner: Banner): void,
    removeBanner(uuid: string): void,
    pushShortSuccessBanner(message: string): void,
    pushShortErrorBanner(message: string): void,
    pushServerErrorBanner(serverResponse: HarmonyAxiosResponse): void,
    getActiveBannerByUUID(uuid: string): Banner | undefined,
    updateCloseInSecondsFormBanner(uuid: string, closeInSeconds: number | undefined): void,
    pushMaintenanceBanner(message: string): void
}

function pushBannerInternal(banner: Banner): void {
    const bannerStore = useBannerStore()
    bannerStore.addBanner(banner)

    if(banner.autoCloseInSeconds) {
        setTimeout(() => {
            if(banner.autoCloseInSeconds) // check if this property was removed after the timeout
                bannerStore.removeBanner(banner.uuid)
        }, banner.autoCloseInSeconds  * 1000)
    }
}

export default function ():BannerContext {
    const bannerStore = useBannerStore()
    return {
        getActiveBanners(): Ref<Banner[]> {
            return computed(() => bannerStore.activeBanners)
        },
        pushBanner(banner: Banner): void {
            pushBannerInternal(banner)
        },
        removeBanner(uuid: string): void {
            bannerStore.removeBanner(uuid)
        },
        pushShortSuccessBanner(message: string): void {
            const banner:Banner = {
                uuid: UUIDUtils.createUUID(),
                type: BannerType.SUCCESS,
                manualClosable: false,
                autoCloseInSeconds: 4,
                message
            }
            pushBannerInternal(banner)
        },
        pushShortErrorBanner(message: string): void {
            const banner:Banner = {
                uuid: UUIDUtils.createUUID(),
                type: BannerType.ERROR,
                manualClosable: false,
                autoCloseInSeconds: 4,
                additionalData: <HarmonyErrorBannerData> {type: HarmonyErrorBannerType.DEFAULT},
                message
            }
            pushBannerInternal(banner)
        },
        pushServerErrorBanner(serverResponse: HarmonyAxiosResponse): void {
            const message = i18n.translate("Server error").build()
            const banner:Banner = {
                uuid: UUIDUtils.createUUID(),
                type: BannerType.ERROR,
                manualClosable: false,
                autoCloseInSeconds: 4,
                additionalData: <HarmonyErrorBannerData> {type: HarmonyErrorBannerType.SERVER_ERROR, data: serverResponse},
                message
            }
            pushBannerInternal(banner)
        },
        getActiveBannerByUUID(uuid: string): Banner | undefined {
            return bannerStore.getActiveBannerByUUID(uuid)
        },
        updateCloseInSecondsFormBanner(uuid: string, closeInSeconds: number | undefined): void {
            bannerStore.updateCloseInSecondsFormBanner(uuid, closeInSeconds)
        },
        pushMaintenanceBanner(message: string): void {
            const banner:Banner = {
                uuid: UUIDUtils.createUUID(),
                type: BannerType.WARNING,
                manualClosable: true,
                message
            }

            pushBannerInternal(banner)
        }
    }
}

