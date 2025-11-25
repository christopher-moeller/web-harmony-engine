import {
    ApiLink,
    ComplexTypeSchema,
    CrudResourceInfoDto,
    ValidationFieldErrorDto
} from "~/CoreApi";
import {AxiosRequestConfig, AxiosResponse, ResponseType} from "axios";

export interface HarmonyAxiosConfig {
    method: string,
    body?: any,
    headers?: any,
    useDefaultErrorHandler?: boolean,
    queryParams?: any,
    responseType?: ResponseType
}

export interface HarmonyAxiosResponse {
    data: any,
    status: number,
    axiosRequestConfig: AxiosRequestConfig,
    success: boolean,
    axiosResponse: AxiosResponse
}

export interface HarmonyRestError {
    text: string,
    axiosResponse: AxiosResponse,
    axiosConfig: AxiosRequestConfig
}

export interface HarmonyContentSwitcherType {
    id: string,
    caption?: string
}
export interface ViewModelAfterSaveListener {
    afterSave(response: HarmonyAxiosResponse): Promise<void>
}

export interface ResourceAfterSaveListener {
    afterSave(response: HarmonyAxiosResponse, isNew: boolean): Promise<void>
}

export interface ResourceSchemaLoadedListener {
    schemaLoaded(resourceSchema: CrudResourceInfoDto): Promise<void>
}

export interface ResourceDataLoadedListener {
    dataLoaded(data: any, isNew: boolean): Promise<void>
}

export interface EffectiveApiLink {
    requestMethod: String,
    link: String,
}

export interface ApiLinkResolver {
    resolveApiLink(apiLink: ApiLink): EffectiveApiLink
}

export interface CustomFormFieldIsEqualCheck {
    isEqual(field1: any, field2: any): boolean
}

export interface RegisteredViewModelField {
    fieldPath: String,
    originalValue: any,
    isDirty: boolean,
    validationError?: ValidationFieldErrorDto,
    schema: ComplexTypeSchema,
}

export enum HarmonyErrorBannerType {
    DEFAULT = "DEFAULT",
    SERVER_ERROR = "SERVER_ERROR"
}

export interface HarmonyErrorBannerData {
    type: HarmonyErrorBannerType,
    data?: any
}

export enum HarmonyButtonType {
    PRIMARY = "primary",
    SECONDARY = "secondary",
    DANGER = "danger"
}

export interface CookieResolver {
    getCookieValue(cookieName: string): string | null | undefined,
    setCookieValue(cookieName: string, value: string | null | undefined): void
}

export enum CookieNames {
    AUTHENTICATION = "authentication",
    LANGUAGE = "language"
}

export interface CustomFilterConfig {
    filterKey: string;
    filterValue: string
}

export interface CustomResourceConfig {
    resourceName: string,
    sortingParam?: string,
    filters: CustomFilterConfig[]
}

export interface SwitchButtonOption {
    id: string,
    iCssClass: string
}

export interface BreadcrumbItem {
    label: string,
    link: string
}

export interface HarmonyTableColumn {
    id: string,
    path?: string,
    caption: string,
    fixedLeft?: boolean,
    isDate?: boolean,
    sortByKey?: string
}

export interface TableFilterContext {
    onValueChanged: (filterKey: String, value: any) => void,
    activeFilters: {key: string, value: string}[]
}

export interface WebContentViewContext {
    isModelingAllowed: boolean,
    hoverComponentStack: number[],
    activeComponentForEdit?: number
}

export interface WebContentContext {
    activeComponentId?: number,
    isInEditMode: boolean,
    hoverComponentStack: number[],
    id?: string
}

export interface WebContentComponentType {
    id: string,
    getCaption: () => string,
    iconId: string,
    createNewInstance: () => any
}

export interface ImageCropperRectangle {
    topLeftX: number,
    topLeftY: number,
    bottomRightX: number,
    bottomRightY: number
}

export interface ImageMultiRectangleData {
    rectangles: ImageCropperRectangle[]
}

export interface Rectangle {
    x: number,
    y: number,
    width: number,
    height: number
}

export interface ImageData {
    naturalWidth: number,
    naturalHeight: number,
    displayWidth: number,
    displayHeight: number
}