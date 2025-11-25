import {defineStore} from "pinia";

export enum BannerType {
    ERROR = "ERROR",
    WARNING = "WARNING",
    SUCCESS = "SUCCESS"
}

export interface Banner {
    uuid: string,
    type: BannerType,
    manualClosable?: boolean,
    autoCloseInSeconds?: number,
    message?: String,
    additionalData?: any
}

interface State {
    activeBanners: Banner[]
}

export const useBannerStore = defineStore('bannerStore', {
    state: (): State => ({ activeBanners: [] }),
    getters: {
        getActiveBanners: (state): Banner[] => state.activeBanners,
        getActiveBannerByUUID: (state) => (uuid: string) => {
            return state.activeBanners.find(b => b.uuid === uuid)
        }
    },
    actions: {
        addBanner(banner: Banner) {
            this.$state.activeBanners.push(banner)
        },
        removeBanner(uuid: string) {
            const foundBanner = this.$state.activeBanners.find(b => b.uuid === uuid)
            if(!foundBanner) {
                throw new Error("Banner with uuid " + uuid + " not found!")
            }
            const index = this.$state.activeBanners.indexOf(foundBanner)
            this.$state.activeBanners.splice(index, 1);
        },
        updateCloseInSecondsFormBanner(uuid: string, closeInSeconds: number | undefined) {
            const foundBanner = this.$state.activeBanners.find(b => b.uuid === uuid)
            foundBanner!.autoCloseInSeconds = closeInSeconds
        }
    },
})