import {defineStore} from "pinia";


interface State {
    isInRoutingMode: boolean
}

export const useRouterStore = defineStore('routerStore', {
    state: (): State => ({ isInRoutingMode: false }),
    getters: {
        getIsInRoutingMode: (state): boolean => state.isInRoutingMode
    },
    actions: {
        setIsInRoutingMode(isInRoutingMode: boolean) {
            this.$state.isInRoutingMode = isInRoutingMode
        }
    },
})