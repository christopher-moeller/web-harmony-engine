import {AbstractResourceDto, ApiResource, FileWebData, ResponseResource, RestSort, SearchResult, Point2D} from "@core/CoreApi";
import { AxiosResponse } from "axios"

// ################ Model declaration start ################

export enum EProjectRouterPage {
	APP_WHISKEY = "app-whiskey",
	APP_WHISKEY_DISTILLERS = "app-whiskey-distillers",
	APP_WHISKEY_DISTILLERS_ID = "app-whiskey-distillers-id",
	APP_WHISKEY_EXTERNALBOTTLERS = "app-whiskey-externalBottlers",
	APP_WHISKEY_EXTERNALBOTTLERS_ID = "app-whiskey-externalBottlers-id",
	APP_WHISKEY_WHISKEYS = "app-whiskey-whiskeys",
	APP_WHISKEY_WHISKEYS_ID = "app-whiskey-whiskeys-id",
	APP_WHISKEY_CONFIGURATION = "app-whiskey-configuration",
	APP_WHISKEY_CONFIGURATION_AMOUNTUNITS = "app-whiskey-configuration-amountUnits",
	APP_WHISKEY_CONFIGURATION_AMOUNTUNITS_ID = "app-whiskey-configuration-amountUnits-id",
	APP_WHISKEY_CONFIGURATION_BARRELTYPES = "app-whiskey-configuration-barrelTypes",
	APP_WHISKEY_CONFIGURATION_BARRELTYPES_ID = "app-whiskey-configuration-barrelTypes-id",
	APP_WHISKEY_CONFIGURATION_CATEGORIES = "app-whiskey-configuration-categories",
	APP_WHISKEY_CONFIGURATION_CATEGORIES_ID = "app-whiskey-configuration-categories-id",
	APP_WHISKEY_CONFIGURATION_COUNTRIES = "app-whiskey-configuration-countries",
	APP_WHISKEY_CONFIGURATION_COUNTRIES_ID = "app-whiskey-configuration-countries-id",
	APP_WHISKEY_CONFIGURATION_CURRENCIES = "app-whiskey-configuration-currencies",
	APP_WHISKEY_CONFIGURATION_CURRENCIES_ID = "app-whiskey-configuration-currencies-id",
	APP_WHISKEY_CONFIGURATION_REGIONS = "app-whiskey-configuration-regions",
	APP_WHISKEY_CONFIGURATION_REGIONS_ID = "app-whiskey-configuration-regions-id",
	APP_WHISKEY_CONFIGURATION_TASTECHARACTERISTICS = "app-whiskey-configuration-tasteCharacteristics",
	APP_WHISKEY_CONFIGURATION_TASTECHARACTERISTICS_ID = "app-whiskey-configuration-tasteCharacteristics-id"
}

export enum EProjectBackendPath {
	API_WHISKEY_AMOUNTUNITS = "/api/whiskey/amountUnits",
	API_WHISKEY_AMOUNTUNITS_ID = "/api/whiskey/amountUnits/{id}",
	API_WHISKEY_BARRELTYPES_ID = "/api/whiskey/barrelTypes/{id}",
	API_WHISKEY_BARRELTYPES = "/api/whiskey/barrelTypes",
	API_WHISKEY_CATEGORIES_ID = "/api/whiskey/categories/{id}",
	API_WHISKEY_CATEGORIES = "/api/whiskey/categories",
	API_WHISKEY_WHISKEYS_PREPROCESS_BOTTLE_IMAGE = "/api/whiskey/whiskeys/preprocess-bottle-image",
	API_WHISKEY_WHISKEYS_ID = "/api/whiskey/whiskeys/{id}",
	API_WHISKEY_WHISKEYS_REMOVE_BACKGROUND_FROM_BOTTLE_IMAGE = "/api/whiskey/whiskeys/remove-background-from-bottle-image",
	API_WHISKEY_WHISKEYS = "/api/whiskey/whiskeys",
	API_WHISKEY_WHISKEYS_CROP_BOTTLE_IMAGE = "/api/whiskey/whiskeys/crop-bottle-image",
	API_WHISKEY_WHISKEYS_ID_IMAGES_FILENAME = "/api/whiskey/whiskeys/{id}/images/{filename}",
	API_WHISKEY_WHISKEYS_ID_UPLOAD_BOTTLE_IMAGE = "/api/whiskey/whiskeys/{id}/upload-bottle-image",
	API_WHISKEY_COUNTRIES = "/api/whiskey/countries",
	API_WHISKEY_COUNTRIES_ID = "/api/whiskey/countries/{id}",
	API_WHISKEY_CURRENCIES_ID = "/api/whiskey/currencies/{id}",
	API_WHISKEY_CURRENCIES = "/api/whiskey/currencies",
	API_WHISKEY_DISTILLERIES_ID = "/api/whiskey/distilleries/{id}",
	API_WHISKEY_DISTILLERIES = "/api/whiskey/distilleries",
	API_WHISKEY_EXTERNALBOTTLERS_ID = "/api/whiskey/externalBottlers/{id}",
	API_WHISKEY_EXTERNALBOTTLERS = "/api/whiskey/externalBottlers",
	API_WHISKEY_REGIONS_ID = "/api/whiskey/regions/{id}",
	API_WHISKEY_REGIONS = "/api/whiskey/regions",
	API_WHISKEY_TASTECHARACTERISTICS_ID = "/api/whiskey/tasteCharacteristics/{id}",
	API_WHISKEY_TASTECHARACTERISTICS = "/api/whiskey/tasteCharacteristics",
}
export enum RequestMethod {
	GET = "GET",
	HEAD = "HEAD",
	POST = "POST",
	PUT = "PUT",
	PATCH = "PATCH",
	DELETE = "DELETE",
	OPTIONS = "OPTIONS",
	TRACE = "TRACE"
}

export enum EDramtasticActorRights {
	WHISKEY_AMOUNT_UNITS_CRUD = "WHISKEY_AMOUNT_UNITS_CRUD",
	WHISKEY_BARREL_TYPES_CRUD = "WHISKEY_BARREL_TYPES_CRUD",
	WHISKEY_CATEGORIES_CRUD = "WHISKEY_CATEGORIES_CRUD",
	WHISKEYS_CRUD = "WHISKEYS_CRUD",
	WHISKEY_COUNTRIES_CRUD = "WHISKEY_COUNTRIES_CRUD",
	WHISKEY_CURRENCIES_CRUD = "WHISKEY_CURRENCIES_CRUD",
	WHISKEY_DISTILLERIES_CRUD = "WHISKEY_DISTILLERIES_CRUD",
	WHISKEY_EXTERNAL_BOTTLERS_CRUD = "WHISKEY_EXTERNAL_BOTTLERS_CRUD",
	WHISKEY_REGIONS_CRUD = "WHISKEY_REGIONS_CRUD",
	WHISKEY_TASTE_CHARACTERISTICS_CRUD = "WHISKEY_TASTE_CHARACTERISTICS_CRUD"
}

export interface WhiskeyCountryDto extends AbstractResourceDto {
	isoCode?: String,
	label?: String,
	description?: String
}

export interface WhiskeyAmountUnitDto extends AbstractResourceDto {
	label?: String,
	description?: String
}

export interface WhiskeyDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	barrelYear?: Number,
	ageOfficially?: Number,
	ageEstimated?: Number,
	fillingDate?: String,
	amount?: Number,
	amountUnit?: ApiResource<any>,
	price?: Number,
	priceCurrency?: ApiResource<any>,
	isFiltered?: Boolean,
	isColored?: Boolean,
	isCaskStrength?: Boolean,
	externalBottler?: ApiResource<any>,
	distillery?: ApiResource<any>,
	barrelType?: ApiResource<any>,
	categories?: ApiResource<any>[],
	tasteCharacteristics?: ApiResource<any>[],
	hasMainImage?: Boolean
}

export interface WhiskeyDistilleryDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	address?: String,
	webLink?: String,
	region?: ApiResource<any>
}

export interface WhiskeyCategoryDto extends AbstractResourceDto {
	label?: String,
	description?: String
}

export interface WhiskeyTasteCharacteristicsDto extends AbstractResourceDto {
	label?: String,
	description?: String
}

export interface WhiskeyBottleImageRemoveBackgroundResponse  {
	imageData?: FileWebData
}

export interface WhiskeyBottleImageCropResponse  {
	imageData?: FileWebData
}

export interface WhiskeyBottleImageRemoveBackgroundRequest  {
	imageData?: FileWebData,
	foregroundRectangles?: WhiskeyBottleImageRectangle[],
	backgroundRectangles?: WhiskeyBottleImageRectangle[]
}

export interface WhiskeyCurrencyDto extends AbstractResourceDto {
	code?: String,
	label?: String
}

export interface WhiskeyExternalBottlerDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	address?: String,
	webLink?: String
}

export interface WhiskeyRegionDto extends AbstractResourceDto {
	label?: String,
	description?: String,
	country?: ApiResource<any>
}

export interface WhiskeyBottleImageCropRequest  {
	imageData?: FileWebData,
	topLeft?: Point2D,
	bottomRight?: Point2D
}

export interface WhiskeyBarrelTypeDto extends AbstractResourceDto {
	label?: String,
	description?: String
}

export interface WhiskeyBottleImageRectangle  {
	topLeft?: Point2D,
	bottomRight?: Point2D
}

export interface WhiskeyBottleImagePreprocessResult  {
	imageData?: FileWebData,
	topLeft?: Point2D,
	bottomRight?: Point2D,
	minWidth?: Number,
	minHeight?: Number
}

export class RestRequestParams {
	queryParameters?: Map<String, any>

	constructor() {
		this.queryParameters = new Map<String, any>();
	}

	static create():RestRequestParams {
		return new RestRequestParams()
	}

	withPage(page: number): RestRequestParams {
		this.addQueryParameter("page", page)
		return this
	}

	withIsPaged(isPaged: boolean): RestRequestParams {
		this.addQueryParameter("isPaged", isPaged)
		return this;
	}

	withSize(size: number): RestRequestParams {
		this.addQueryParameter("size", size)
		return this;
	}

	withAttributes(attributes: String[]): RestRequestParams {
		this.addQueryParameter("attributes", attributes.join(","))
		return this;
	}

	withSorts(sorts: RestSort[]): RestRequestParams {
		const sortFragments:string[] = []
		for(let sort of sorts) {
			sortFragments.push(sort.name + ":" + sort.order)
		}
		if(sortFragments.length > 0) {
			this.addQueryParameter("sort", sortFragments.join(","))
		}
		return this;
	}

	addQueryParameter(key: string, value: any): RestRequestParams {
		this.queryParameters!.set(key, value);
		return this;
	}

	toQueryString(): string {
		const queryFragments:string[] = []
		for (let entry of Array.from(this.queryParameters!.entries())) {
			let key = entry[0];
			let value = entry[1];
			queryFragments.push(key + "=" + value)
		}

		return queryFragments.length > 0 ? "?" + queryFragments.join("&") : ""
	}
}

// ################ Model declaration end ################


export interface ApiResolver {
	resolveRequest(apiMethod: string, apiPath: string, body?: any): Promise<any>,
	resolveUrlForRestRequestParams(baseUrl: string, restRequestParams: RestRequestParams):string
}


// ################ API Method declaration start ################

export default function(apiResolver: ApiResolver) {
	return {
		api() {
			return {
				getWhiskeyAmountUnitApi() {
					return {

						async createNewEntry(body: WhiskeyAmountUnitDto):Promise<AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/amountUnits", body)
							return <AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/amountUnits", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/amountUnits/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/amountUnits/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyAmountUnitDto):Promise<AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/amountUnits/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyAmountUnitDto>, any>> response
						},
					}
				},

				getWhiskeyBarrelTypeApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/barrelTypes/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyBarrelTypeDto):Promise<AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/barrelTypes/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> response
						},
						async createNewEntry(body: WhiskeyBarrelTypeDto):Promise<AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/barrelTypes", body)
							return <AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/barrelTypes/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyBarrelTypeDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/barrelTypes", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWhiskeyCategoryApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/categories/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyCategoryDto):Promise<AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/categories/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> response
						},
						async createNewEntry(body: WhiskeyCategoryDto):Promise<AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/categories", body)
							return <AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/categories/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyCategoryDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/categories", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWhiskeyApi() {
					return {

						async preprocessBottleImage(body: FileWebData):Promise<AxiosResponse<WhiskeyBottleImagePreprocessResult, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/whiskeys/preprocess-bottle-image", body)
							return <AxiosResponse<WhiskeyBottleImagePreprocessResult, any>> response
						},
						async updateEntry(id: String, body: WhiskeyDto):Promise<AxiosResponse<ResponseResource<WhiskeyDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/whiskeys/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyDto>, any>> response
						},
						async removeBackgroundFromBottleImage(body: WhiskeyBottleImageRemoveBackgroundRequest):Promise<AxiosResponse<WhiskeyBottleImageRemoveBackgroundResponse, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/whiskeys/remove-background-from-bottle-image", body)
							return <AxiosResponse<WhiskeyBottleImageRemoveBackgroundResponse, any>> response
						},
						async createNewEntry(body: WhiskeyDto):Promise<AxiosResponse<ResponseResource<WhiskeyDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/whiskeys", body)
							return <AxiosResponse<ResponseResource<WhiskeyDto>, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/whiskeys/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/whiskeys", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async cropBottleImage(body: WhiskeyBottleImageCropRequest):Promise<AxiosResponse<WhiskeyBottleImageCropResponse, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/whiskeys/crop-bottle-image", body)
							return <AxiosResponse<WhiskeyBottleImageCropResponse, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/whiskeys/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async uploadBottleImage(id: String, body: FileWebData):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", `/api/whiskey/whiskeys/${id}/upload-bottle-image`, body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

				getWhiskeyCountryApi() {
					return {

						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/countries", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/countries/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> response
						},
						async createNewEntry(body: WhiskeyCountryDto):Promise<AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/countries", body)
							return <AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/countries/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyCountryDto):Promise<AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/countries/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyCountryDto>, any>> response
						},
					}
				},

				getWhiskeyCurrencyApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/currencies/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyCurrencyDto):Promise<AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/currencies/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> response
						},
						async createNewEntry(body: WhiskeyCurrencyDto):Promise<AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/currencies", body)
							return <AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/currencies/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyCurrencyDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/currencies", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWhiskeyDistilleryApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/distilleries/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/distilleries/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyDistilleryDto):Promise<AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/distilleries/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> response
						},
						async createNewEntry(body: WhiskeyDistilleryDto):Promise<AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/distilleries", body)
							return <AxiosResponse<ResponseResource<WhiskeyDistilleryDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/distilleries", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWhiskeyExternalBottlerApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/externalBottlers/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> response
						},
						async createNewEntry(body: WhiskeyExternalBottlerDto):Promise<AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/externalBottlers", body)
							return <AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/externalBottlers/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async updateEntry(id: String, body: WhiskeyExternalBottlerDto):Promise<AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/externalBottlers/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyExternalBottlerDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/externalBottlers", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
					}
				},

				getWhiskeyRegionApi() {
					return {

						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/regions/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> response
						},
						async createNewEntry(body: WhiskeyRegionDto):Promise<AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/regions", body)
							return <AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> response
						},
						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/regions/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/regions", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async updateEntry(id: String, body: WhiskeyRegionDto):Promise<AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/regions/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyRegionDto>, any>> response
						},
					}
				},

				getWhiskeyTasteCharacteristicsApi() {
					return {

						async deleteEntry(id: String):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("DELETE", `/api/whiskey/tasteCharacteristics/${id}`, undefined)
							return <AxiosResponse<any, any>> response
						},
						async createNewEntry(body: WhiskeyTasteCharacteristicsDto):Promise<AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/whiskey/tasteCharacteristics", body)
							return <AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> response
						},
						async getAllEntries(restRequestParams: RestRequestParams):Promise<AxiosResponse<SearchResult, any>> {
							const response = await apiResolver.resolveRequest("GET", apiResolver.resolveUrlForRestRequestParams("/api/whiskey/tasteCharacteristics", restRequestParams), undefined)
							return <AxiosResponse<SearchResult, any>> response
						},
						async updateEntry(id: String, body: WhiskeyTasteCharacteristicsDto):Promise<AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> {
							const response = await apiResolver.resolveRequest("PUT", `/api/whiskey/tasteCharacteristics/${id}`, body)
							return <AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> response
						},
						async getEntryById(id: String):Promise<AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> {
							const response = await apiResolver.resolveRequest("GET", `/api/whiskey/tasteCharacteristics/${id}`, undefined)
							return <AxiosResponse<ResponseResource<WhiskeyTasteCharacteristicsDto>, any>> response
						},
					}
				},

			}
		}
	}
}

// ################ API Method declaration end ################"