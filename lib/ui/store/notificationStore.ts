import {defineStore} from "pinia";
import {ActorPersonalNotificationInfoDto} from "@core/CoreApi";
import useCoreApi from "~/composables/useCoreApi";
import {CookieResolver} from "~/utils/HarmonyTypes";

interface State {
    notificationInfo: ActorPersonalNotificationInfoDto
}

export const useNotificationStore = defineStore('notificationStore', {
    state: (): State => ({ notificationInfo: {} }),
    getters: {
        getNotificationInfo: (state): ActorPersonalNotificationInfoDto => state.notificationInfo
    },
    actions: {
        async init(cookieResolver: CookieResolver) {
            const coreApi = useCoreApi(cookieResolver)
            this.notificationInfo = (await coreApi.api().getPersonalNotificationsApi().getActorPersonalNotificationInfo()).data
        },
    },
})