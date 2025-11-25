import {ApiLink, CrudResourceInfoDto, ValidationFieldErrorDto, ValidationResultDto} from "~/CoreApi";
import {
    ApiLinkResolver, CookieResolver,
    EffectiveApiLink, HarmonyAxiosResponse,
    ResourceAfterSaveListener,
    ResourceDataLoadedListener,
    ResourceSchemaLoadedListener
} from "~/utils/HarmonyTypes";
import {HarmonyResourceState, useHarmonyResourceStore} from "~/store/form/harmonyResourceStore";
import {Ref} from "vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import {ComputedGetter} from "@vue/reactivity";
import {WritableComputedRefValue} from "vue/macros";
import useCoreApi from "~/composables/useCoreApi";
import useHarmonyFetch from "~/composables/useHarmonyFetch";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

export interface ResourceContextConfig {
    getTemplateLinkResolver: ApiLinkResolver,
    getByIdLinkResolver: ApiLinkResolver,
    createNewLink: ApiLinkResolver,
    updateLink: ApiLinkResolver,
    deleteLinkResolver: ApiLinkResolver,
    afterSaveListeners: ResourceAfterSaveListener[],
    schemaLoadedListeners: ResourceSchemaLoadedListener[],
    dataLoadedListeners: ResourceDataLoadedListener[],
    cookieResolver: CookieResolver
}

const defaultApiLinkResolver: ApiLinkResolver = {
    resolveApiLink(apiLink: ApiLink): EffectiveApiLink {
        return {
            requestMethod: apiLink.requestMethod+"",
            link: apiLink.link+""
        }
    }
}

export interface ResourceContext {
    hardFetchApiState(): Promise<void>,
    getLocalEffectiveTargetData(): any,
    getLocalState(): HarmonyResourceState,
    getReactiveDirtyState():Ref<boolean>,
    getReactiveIsSavingState():Ref<boolean>,
    revertChanges(): void,
    saveToApi(): Promise<HarmonyAxiosResponse>,
    getContextConfig(): ResourceContextConfig,
    hasLocalStateInStore(): boolean,
    deleteResource(): Promise<HarmonyAxiosResponse>,
    isNewResource(): boolean,
    fetchIfNotInLocalStore(): Promise<void>,
    getFieldValueRef(fieldPath: string): Ref<any>,
    getResourceName(): string,
    getResourceId(): string
}

function isNewResourceInternal(resourceId: string): boolean {
    return resourceId === "new"
}

async function getCrudResourceInfo(resourceName: string, config: ResourceContextConfig): Promise<CrudResourceInfoDto> {
    const response = await useCoreApi(config.cookieResolver).api().getAppResourceApi().getResourceByName(resourceName)
    const schema:CrudResourceInfoDto = response.data
    const listeners = config.schemaLoadedListeners
    for (let listener of listeners) {
        await listener.schemaLoaded(schema)
    }

    return schema
}

async function fireDataLoadedEvent(data: any, isNew: boolean, config: ResourceContextConfig) {
    const listeners = config.dataLoadedListeners
    for (let listener of listeners) {
        await listener.dataLoaded(data, isNew)
    }
}

async function fetchNewResourceByTemplate(resourceName: string, config: ResourceContextConfig) {
    const resourceSchema:CrudResourceInfoDto = await getCrudResourceInfo(resourceName, config)
    const apiLink:ApiLink = resourceSchema.getTemplateLink!
    const apiLinkResolver:ApiLinkResolver = config.getTemplateLinkResolver
    const effectiveApiLink:EffectiveApiLink = apiLinkResolver.resolveApiLink(apiLink)
    const resourceResponse:HarmonyAxiosResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+""})
    const templateData:any = resourceResponse.data
    const state:HarmonyResourceState = {
        resourceName: resourceName,
        resourceId: "new",
        schema: resourceSchema,
        data: templateData,
        //@ts-ignore
        originalData: JSON.parse(JSON.stringify(templateData)),
        registeredFields: [],
        isCurrentlySaving: false
    }

    const store = useHarmonyResourceStore()
    store.removeIfExists(state)
    store.addViewModelState(state)

    await fireDataLoadedEvent(templateData, true, config)
}

async function fetchExistingResource(resourceName: string, resourceId: string, config: ResourceContextConfig) {
    const resourceSchema:CrudResourceInfoDto = await getCrudResourceInfo(resourceName, config)
    const apiLink:ApiLink = resourceSchema.getByIdLink!
    const apiLinkResolver:ApiLinkResolver = config.getByIdLinkResolver
    const effectiveApiLink:EffectiveApiLink = apiLinkResolver.resolveApiLink(apiLink)
    const resourceResponse:HarmonyAxiosResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+""})
    const rawData:any = resourceResponse.data.data.data
    const state:HarmonyResourceState = {
        resourceName: resourceName,
        resourceId: resourceId,
        schema: resourceSchema,
        data: rawData,
        //@ts-ignore
        originalData: JSON.parse(JSON.stringify(rawData)),
        registeredFields: [],
        isCurrentlySaving: false
    }

    const store = useHarmonyResourceStore()
    store.removeIfExists(state)
    store.addViewModelState(state)

    await fireDataLoadedEvent(rawData, false, config)
}
async function hardFetchApiStateInternal(resourceName: string, resourceId: string, config: ResourceContextConfig) {
    isNewResourceInternal(resourceId) ? await fetchNewResourceByTemplate(resourceName, config) : await fetchExistingResource(resourceName, resourceId, config)
}

function findStateIfExists(resourceName: string, resourceId: string): HarmonyResourceState | undefined {
    return useHarmonyResourceStore().getResourceStateRef(resourceName, resourceId).value
}

function findStateOrThrow(resourceName: string, resourceId: string): HarmonyResourceState {
    const state = findStateIfExists(resourceName, resourceId)
    if(!state) {
        throw new Error("State for resource with name " + resourceName + " and id " + resourceId + " not found")
    }
    return state
}

function clearStateIfExisting(resourceName: string, resourceId: string) {
    const state = findStateIfExists(resourceName, resourceId)
    if(state) {
        useHarmonyResourceStore().removeIfExists(state)
    }
}

function getLocalEffectiveTargetDataInternal(resourceName: string, resourceId: string): any {
    return findStateOrThrow(resourceName, resourceId).data
}

function getReactiveDirtyStateInternal(resourceName: string, resourceId: string): Ref<boolean> {
    return computed(() => {
        const state = findStateIfExists(resourceName, resourceId)
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

function revertChangesInternal(resourceName: string, resourceId: string): void {
    const state = findStateOrThrow(resourceName, resourceId)
    const targetData = state.data
    state.registeredFields.forEach(field => {
        const fieldComputed = ComputedUtils.createComputedValueForObjectAndFieldPath(targetData, field.fieldPath+"")
        fieldComputed.value = JSON.parse(JSON.stringify(field.originalValue))
        field.validationError = undefined
    })
}

function applyValidationErrors(apiResponse: HarmonyAxiosResponse, state: HarmonyResourceState) {
    if(!apiResponse.success) {
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

async function saveNewResource(resourceName: string, resourceId: string, config: ResourceContextConfig):Promise<HarmonyAxiosResponse> {
    const resourceSchema:CrudResourceInfoDto = await getCrudResourceInfo(resourceName, config)
    const apiLink:ApiLink = resourceSchema.createNewLink!
    const apiLinkResolver:ApiLinkResolver = config.createNewLink
    const effectiveApiLink:EffectiveApiLink = apiLinkResolver.resolveApiLink(apiLink)

    const state: HarmonyResourceState = findStateOrThrow(resourceName, resourceId)

    state.isCurrentlySaving = true
    const saveResponse:HarmonyAxiosResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+"", body: state.data})
    state.isCurrentlySaving = false

    applyValidationErrors(saveResponse, state)

    const listeners = config.afterSaveListeners
    for (let listener of listeners) {
        await listener.afterSave(saveResponse, true)
    }

    if(saveResponse.success) {
        clearStateIfExisting(resourceName, resourceId)
    }

    return saveResponse
}

async function updateResource(resourceName: string, resourceId: string, config: ResourceContextConfig):Promise<HarmonyAxiosResponse> {
    const resourceSchema:CrudResourceInfoDto = await getCrudResourceInfo(resourceName, config)
    const apiLink:ApiLink = resourceSchema.updateLink!
    const apiLinkResolver:ApiLinkResolver = config.updateLink
    const effectiveApiLink:EffectiveApiLink = apiLinkResolver.resolveApiLink(apiLink)

    const state: HarmonyResourceState = findStateOrThrow(resourceName, resourceId)

    state.isCurrentlySaving = true
    const saveResponse:HarmonyAxiosResponse = await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+"", body: state.data})
    state.isCurrentlySaving = false

    applyValidationErrors(saveResponse, state)

    const listeners = config.afterSaveListeners
    for (let listener of listeners) {
        await listener.afterSave(saveResponse, false)
    }

    return saveResponse
}

async function saveToApiInternal(resourceName: string, resourceId: string, config: ResourceContextConfig):Promise<HarmonyAxiosResponse> {
    return isNewResourceInternal(resourceId) ? await saveNewResource(resourceName, resourceId, config) : await updateResource(resourceName, resourceId, config)
}

async function deleteResource(resourceName: string, config: ResourceContextConfig): Promise<HarmonyAxiosResponse> {
    const resourceSchema:CrudResourceInfoDto = await getCrudResourceInfo(resourceName, config)
    const apiLink:ApiLink = resourceSchema.deleteLink!
    const apiLinkResolver:ApiLinkResolver = config.deleteLinkResolver
    const effectiveApiLink:EffectiveApiLink = apiLinkResolver.resolveApiLink(apiLink)

    return await useHarmonyFetch(config.cookieResolver).fetch(effectiveApiLink.link+"", {method: effectiveApiLink.requestMethod+""})
}

function getFieldValueRefInternal(resourceName: string, resourceId: string, fieldPath: string): WritableComputedRefValue<any> {
    const state = useHarmonyResourceStore().getResourceStateRef(resourceName, resourceId)
    const get:ComputedGetter<any> = () => state.value ? ComputedUtils.createComputedValueForObjectAndFieldPath(state.value.data, fieldPath).value : undefined
    const set:ComputedGetter<any> = (value:any) => {
        if(state.value) {
            ComputedUtils.createComputedValueForObjectAndFieldPath(state.value.data, fieldPath).value = value
        } else {
            throw new Error("Data for resource " + resourceName + " with id " + resourceId + " is not initialized!")
        }
    }
    return computed({get, set})
}

export default function (resourceName: string, resourceId: string, cookieResolver?: CookieResolver):ResourceContext {

    const internalCookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
    const resourceConfig:ResourceContextConfig = {
        getTemplateLinkResolver: defaultApiLinkResolver,
        getByIdLinkResolver: defaultApiLinkResolver,
        createNewLink: defaultApiLinkResolver,
        updateLink: defaultApiLinkResolver,
        deleteLinkResolver: defaultApiLinkResolver,
        afterSaveListeners: [],
        schemaLoadedListeners: [],
        dataLoadedListeners: [],
        cookieResolver: internalCookieResolver
    }

    return {
        async fetchIfNotInLocalStore(): Promise<void> {
            if(!findStateIfExists(resourceName, resourceId)) {
                await hardFetchApiStateInternal(resourceName, resourceId, resourceConfig)
            }
            const data:any = getLocalEffectiveTargetDataInternal(resourceName, resourceId)
            await fireDataLoadedEvent(data, isNewResourceInternal(resourceId), resourceConfig)
        },
        async hardFetchApiState() {
            await hardFetchApiStateInternal(resourceName, resourceId, resourceConfig)
        },
        getLocalEffectiveTargetData(): any {
            return getLocalEffectiveTargetDataInternal(resourceName, resourceId)
        },
        getLocalState(): HarmonyResourceState {
            return findStateOrThrow(resourceName, resourceId)
        },
        getReactiveDirtyState():Ref<boolean> {
            return getReactiveDirtyStateInternal(resourceName, resourceId)
        },
        getReactiveIsSavingState(): Ref<boolean> {
            return computed(() => {
                const state = findStateIfExists(resourceName, resourceId)
                return !!state && state.isCurrentlySaving
            })
        },
        revertChanges(): void {
            revertChangesInternal(resourceName, resourceId)
        },
        async saveToApi(): Promise<HarmonyAxiosResponse> {
            return await saveToApiInternal(resourceName, resourceId, resourceConfig)
        },
        getContextConfig(): ResourceContextConfig {
            return resourceConfig
        },
        hasLocalStateInStore(): boolean {
            return !!findStateIfExists(resourceName, resourceId)
        },
        async deleteResource(): Promise<HarmonyAxiosResponse> {
            return await deleteResource(resourceName, resourceConfig)
        },
        isNewResource(): boolean {
            return isNewResourceInternal(resourceId)
        },
        getFieldValueRef(fieldPath: string): Ref<any> {
            return getFieldValueRefInternal(resourceName, resourceId, fieldPath)
        },
        getResourceName(): string {
          return resourceName
        },
        getResourceId(): string {
            return resourceId
        }
    }
}