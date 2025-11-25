import {defineStore} from "pinia";
import {ComplexTypeSchema} from "~/CoreApi";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";

export interface HarmonyViewModelState {
    uniqueId: string,
    schema: ComplexTypeSchema,
    data: any,
    originalData: any,
    registeredFields: RegisteredViewModelField[],
    isCurrentlySaving: boolean
}

interface State {
    viewModelStates: HarmonyViewModelState[]
}

export const useHarmonyViewModelStore = defineStore('harmonyViewModelStore', {
    state: (): State => ({ viewModelStates: [] }),
    getters: {
        getViewModelStates: (state): HarmonyViewModelState[] => state.viewModelStates
    },
    actions: {
        addViewModelState(viewModelState: HarmonyViewModelState) {
            this.$state.viewModelStates.push(viewModelState)
        },
        clear() {
            this.$state.viewModelStates = []
        },
        removeIfExists(entry: HarmonyViewModelState) {
            const existingEntry = this.$state.viewModelStates.find(storeState => storeState.uniqueId === entry.uniqueId)
            if (existingEntry) {
                this.$state.viewModelStates.splice(this.$state.viewModelStates.indexOf(existingEntry), 1);
            }
        },
        changeIsSavingState(uniqueId: string, value: boolean) {
            const state = this.$state.viewModelStates.find(e => e.uniqueId === uniqueId)!
            state.isCurrentlySaving = value
        }
    },
})