import {ApiLink, ValidationFieldErrorDto, ValidationResultDto} from "~/CoreApi";
import {HarmonyViewModelState, useHarmonyViewModelStore} from "~/store/form/harmonyViewModelStore";
import {Ref} from "vue";
import {
    ApiLinkResolver, CookieResolver,
    EffectiveApiLink,
    HarmonyAxiosResponse,
    ViewModelAfterSaveListener
} from "~/utils/HarmonyTypes";
import useCoreApi from "~/composables/useCoreApi";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";
import {ComputedUtils} from "~/utils/ComputedUtils";

export interface ViewModelContextConfig {
    loadLinkResolver: ApiLinkResolver,
    saveLinkResolver: ApiLinkResolver,
    afterSaveListeners: ViewModelAfterSaveListener[],
    cookieResolver: CookieResolver
}

const defaultApiLinkResolver:ApiLinkResolver = {
    resolveApiLink(apiLink: ApiLink): EffectiveApiLink {
        return {
            requestMethod: apiLink.requestMethod!,
            link: apiLink.link! + ""
        }
    }
}

export interface ViewModelContext {
    hardFetchApiState(): Promise<void>
    getLocalEffectiveTargetData(): any,
    getLocalState(): HarmonyViewModelState,
    getReactiveDirtyState(): Ref<boolean>,
    getReactiveIsSavingState(): Ref<boolean>,
    revertChanges(): void,
    saveToApi(): Promise<HarmonyAxiosResponse>,
    hasLocalStateInStore(): boolean,
    clearListener(): void,
    getContextConfig(): ViewModelContextConfig
    getViewModelId(): string,
}

function setStateToStore(state: HarmonyViewModelState) {
    const store = useHarmonyViewModelStore()
    store.removeIfExists(state)
    store.addViewModelState(state)
}
async function hardFetchApiStateForRawViewModel(viewModelId: string, config: ViewModelContextConfig) {
    const viewModelSchemaResponse = await useCoreApi(config.cookieResolver).api().getAppViewModelApi().getViewModelByName(viewModelId)
    const effectiveApiLink = config.loadLinkResolver.resolveApiLink(viewModelSchemaResponse.data.loadLink!)
    const viewModelInitialDataResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+""})
    if(!viewModelInitialDataResponse.success)
        throw viewModelInitialDataResponse

    setStateToStore({
        uniqueId: viewModelId,
        schema: viewModelSchemaResponse.data.schema!,
        data: viewModelInitialDataResponse.data,
        //@ts-ignore
        originalData: JSON.parse(JSON.stringify(viewModelInitialDataResponse.data)),
        registeredFields: [],
        isCurrentlySaving: false
    })
}

async function hardFetchApiStateInternal(viewModelId: string, config: ViewModelContextConfig) {
    await hardFetchApiStateForRawViewModel(viewModelId, config)
}

function findStateIfExists(uniqueId: string): HarmonyViewModelState | undefined {
    const states:HarmonyViewModelState[] = useHarmonyViewModelStore().getViewModelStates
    return states.find(storeState => storeState.uniqueId === uniqueId)
}

function findStateOrThrow(uniqueId: string): HarmonyViewModelState {
    const state = findStateIfExists(uniqueId)
    if(!state) {
        throw new Error("State for view model with id " + uniqueId + " not found")
    }
    return state
}

function getLocalEffectiveTargetData(viewModelId: string): any {
    return findStateOrThrow(viewModelId).data
}

function getReactiveDirtyStateInternal(viewModelId: string): Ref<boolean> {
    return computed(() => {
        const state = findStateIfExists(viewModelId)
        if(!state) {
            return false
        }
        const registeredFields = state.registeredFields
        if(!registeredFields)
            return false;
        for (let registeredField of state.registeredFields) {
            if(registeredField.isDirty) {
                return true
            }
        }
        return false
    })
}

function revertChangesInternal(viewModelId: string): void {
    const targetData = getLocalEffectiveTargetData(viewModelId)
    const state = findStateOrThrow(viewModelId)
    state.registeredFields.forEach(field => {
        const fieldComputed = ComputedUtils.createComputedValueForObjectAndFieldPath(targetData, field.fieldPath+"")
        fieldComputed.value = JSON.parse(JSON.stringify(field.originalValue))
        field.validationError = undefined
    })
}

function applyValidationErrors(apiResponse: HarmonyAxiosResponse, state: HarmonyViewModelState) {
    if(apiResponse.status !== 200) {
        const data = apiResponse.data
        if(data.type === "VALIDATION") {
            state.registeredFields.forEach(rf => rf.validationError = undefined) // clear old errors
            const validationResult:ValidationResultDto = <ValidationResultDto> data.data
            validationResult.fields?.forEach((fieldError: ValidationFieldErrorDto) => {
                const path = fieldError.fieldPath!
                const registeredField = state.registeredFields.find(rf => rf.fieldPath === path)
                if(registeredField) {
                    registeredField.validationError = fieldError
                }
            })
        }
    }
}

async function saveRawViewModelToApi(viewModelId: string, config: ViewModelContextConfig): Promise<HarmonyAxiosResponse> {
    const viewModelSchemaResponse = await useCoreApi(config.cookieResolver).api().getAppViewModelApi().getViewModelByName(viewModelId)
    const saveLink = viewModelSchemaResponse.data.saveLink!
    const effectiveApiLink = config.saveLinkResolver.resolveApiLink(saveLink)

    const state = findStateOrThrow(viewModelId)

    state.isCurrentlySaving = true
    const saveResponse:HarmonyAxiosResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod!+"", body: state.data})
    state.isCurrentlySaving = false

    applyValidationErrors(saveResponse, state)

    return saveResponse
}

async function saveToApiInternal(viewModelId: string, config: ViewModelContextConfig): Promise<HarmonyAxiosResponse> {
    const response:HarmonyAxiosResponse = await saveRawViewModelToApi(viewModelId, config)
    const listeners = config.afterSaveListeners
    for (let listener of listeners) {
        await listener.afterSave(response)
    }
    return response;
}

export default function (viewModelId: string, cookieResolver?: CookieResolver):ViewModelContext {

    const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    const config:ViewModelContextConfig = {
        saveLinkResolver: defaultApiLinkResolver,
        loadLinkResolver: defaultApiLinkResolver,
        afterSaveListeners: [],
        cookieResolver: internalCookieResolver
    }

    return {
        async hardFetchApiState() {
            await hardFetchApiStateInternal(viewModelId, config)
        },
        getLocalEffectiveTargetData(): any {
            return getLocalEffectiveTargetData(viewModelId)
        },
        getLocalState(): HarmonyViewModelState {
            return findStateOrThrow(viewModelId)
        },
        hasLocalStateInStore(): boolean {
            return !!findStateIfExists(viewModelId)
        },
        getReactiveDirtyState():Ref<boolean> {
            return getReactiveDirtyStateInternal(viewModelId)
        },
        getReactiveIsSavingState(): Ref<boolean> {
            return computed(() => {
                const state = findStateIfExists(viewModelId)
                return !!state && state.isCurrentlySaving
            })
        },
        revertChanges(): void {
            revertChangesInternal(viewModelId)
        },
        async saveToApi(): Promise<HarmonyAxiosResponse> {
            return await saveToApiInternal(viewModelId, config)
        },
        clearListener(): void {
            config.afterSaveListeners = []
        },
        getContextConfig(): ViewModelContextConfig {
            return config
        },
        getViewModelId(): string {
            return viewModelId
        }
    }
}