package com.webharmony.starter.builder.files.ui;

import com.webharmony.starter.builder.ProjectBuildingContext;
import com.webharmony.starter.builder.VirtualFile;

public class UIComposableUseProjectApiFile extends VirtualFile {

    private static final String TEMPLATE = """
            import {ApiResolver, RestRequestParams} from "@core/CoreApi";
            import useHarmonyFetch from "@core/composables/useHarmonyFetch";
            import {CookieResolver} from "@core/utils/HarmonyTypes";
            import useHarmonyCookies from "@core/composables/useHarmonyCookies";
            import projectApi from "~/ProjectApi";
                        
                        
            export default function (cookieResolver?: CookieResolver) {
                const internalCookieResolver:CookieResolver = cookieResolver ? cookieResolver : useHarmonyCookies()
                const harmonyFetch = useHarmonyFetch(internalCookieResolver)
                const apiResolver:ApiResolver = {
                    resolveRequest(apiMethod: string, apiPath: string, body?: any): Promise<any> {
                        return harmonyFetch.fetch(apiPath, {
                            method: apiMethod,
                            body: body
                        })
                    },
                    resolveUrlForRestRequestParams(baseUrl: string, restRequestParams: RestRequestParams): string {
                        return baseUrl + restRequestParams.toQueryString();
                    }
                }
                return projectApi(apiResolver)
            }
                        
                        
            """;

    public UIComposableUseProjectApiFile() {
        super("useProjectApi.ts");
    }

    @Override
    public String createFileContent(ProjectBuildingContext projectBuildingContext) {
        return TEMPLATE;
    }
}
