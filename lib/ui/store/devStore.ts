import {defineStore} from "pinia";


interface State {
    hasDevProfile: boolean,
    isDevSidePanelOpen: boolean,
    showViewModelFieldInfo: boolean,
    showI18nKeys: boolean
}

export const useDevStore = defineStore('devState', {
    state: (): State => ({ hasDevProfile: true, isDevSidePanelOpen: false, showViewModelFieldInfo: false, showI18nKeys: false }),
    getters: {
        getHasDevProfile: (state) => state.hasDevProfile,
        getIsDevSidePanelOpen: state => state.isDevSidePanelOpen,
        getShowViewModelFieldInfo: state => state.showViewModelFieldInfo,
        getShowI18nKeys: state => state.showI18nKeys
    },
    actions: {
        toggleSidePanel() {
            this.isDevSidePanelOpen = !this.isDevSidePanelOpen
        }
    },
})