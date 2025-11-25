import {AbstractResourceDto, ApiResource, FileWebData, ResponseResource, RestSort, SearchResult, Point2D} from "@core/CoreApi";
import { AxiosResponse } from "axios"

// ################ Model declaration start ################

export enum EProjectRouterPage {
	INDEX = "index",
	FORBIDDEN = "forbidden",
	PAGENOTFOUND = "pageNotFound",
	LANGUAGE = "language"
}

export enum EProjectBackendPath {
	API_STARTER = "/api/starter"
}
export enum EStarterActorRights {
	STARTER_TEST_RIGHT = "STARTER_TEST_RIGHT"
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

export interface CreateHarmonyProjectRequest  {
	projectTechnicalName?: String,
	projectShortName?: String,
	projectLongName?: String,
	datasourcePort?: String,
	datasourceUsername?: String,
	datasourcePassword?: String,
	apiImageName?: String,
	uiImageName?: String,
	nginxImageName?: String
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
				getStarterApi() {
					return {

						async createProject(body: CreateHarmonyProjectRequest):Promise<AxiosResponse<any, any>> {
							const response = await apiResolver.resolveRequest("POST", "/api/starter", body)
							return <AxiosResponse<any, any>> response
						},
					}
				},

			}
		}
	}
}

// ################ API Method declaration end ################"