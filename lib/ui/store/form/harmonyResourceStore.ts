import {defineStore} from "pinia";
import {CrudResourceInfoDto} from "~/CoreApi";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";

export interface HarmonyResourceState {
    resourceId: string,
    resourceName: string,
    schema: CrudResourceInfoDto,
    data: any,
    originalData: any,
    registeredFields: RegisteredViewModelField[],
    isCurrentlySaving: boolean
}

interface State {
    resourceStates: HarmonyResourceState[]
}

export const useHarmonyResourceStore = defineStore('harmonyResourceStore', {
    state: (): State => ({ resourceStates: [] }),
    getters: {
        getResourceStates: (state): HarmonyResourceState[] => {
            return state.resourceStates
        },
        getResourceStateRef: (state) => (resourceName: string, resourceId: string) => {
            return computed(() => state.resourceStates.find(s => s.resourceName === resourceName && s.resourceId === resourceId))
        }
    },
    actions: {
        addViewModelState(viewModelState: HarmonyResourceState) {
            this.$state.resourceStates.push(viewModelState)
        },
        clear() {
            this.$state.resourceStates = []
        },
        removeIfExists(entry: HarmonyResourceState) {
            const existingEntry = this.$state.resourceStates.find(storeState => storeState.resourceName === entry.resourceName && storeState.resourceId === entry.resourceId)
            if (existingEntry) {
                this.$state.resourceStates.splice(this.$state.resourceStates.indexOf(existingEntry), 1);
            }
        },
        changeIsSavingState(resourceName: string, resourceId: string, value: boolean) {
            const state = this.$state.resourceStates.find(e => e.resourceName === resourceName && e.resourceId === resourceId)!
            state.isCurrentlySaving = value
        }
    }
})