import {defineStore} from "pinia";
import {CustomResourceConfig} from "~/utils/HarmonyTypes";


interface State {
    resourceTableFilters: CustomResourceConfig[]
}

export const useCustomFilterStore = defineStore('customFilterStore', {
    state: (): State => ({ resourceTableFilters: [] }),
    getters: {
        getResourceTableFilterConfig: (state) => (resourceName: string): CustomResourceConfig | undefined => {
            return state.resourceTableFilters.find(config => config.resourceName === resourceName)
        },
    },
    actions: {
        addResourceTableFilterConfig(customResourceConfig: CustomResourceConfig) {
            this.$state.resourceTableFilters.push(customResourceConfig)
        }
    },
})