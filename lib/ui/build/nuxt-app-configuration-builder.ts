import {NuxtPage} from "@nuxt/schema";
import {FrontendProjectConfiguration, PageJson} from "~/CoreApi";
import path from "path"
import fs from "fs"

function createNewEmptyNuxtPage(pageJson: PageJson):NuxtPage {

    if(!pageJson.path)
        throw new Error("Page with id " + pageJson.id + " has no defined path")

    return {
        name: pageJson.id!+"",
        path: pageJson.path!+""
    }
}

function findOrCreateNuxtPage(nuxtPages: NuxtPage[], pageJson: PageJson):NuxtPage {
    const definedPage = nuxtPages.find(p => p.name === pageJson.id)
    return definedPage ? definedPage : createNewEmptyNuxtPage(pageJson)
}
function buildNewNuxtPages(currentNuxtPages: NuxtPage[], jsonPages:PageJson[], coreBuildDirectory: string, projectBuildDirectory: string):NuxtPage[] {
    const resultPages:NuxtPage[] = []
    for (const jsonPage of jsonPages) {
        const nuxtPage:NuxtPage = findOrCreateNuxtPage(currentNuxtPages, jsonPage)

        const componentFilePathFromJson = jsonPage?.frontendComponent?.componentPath
        if(componentFilePathFromJson) {
            const isCoreComponentByJsonDef = !!jsonPage?.frontendComponent?.isCoreComponent
            const basePath = isCoreComponentByJsonDef ? coreBuildDirectory : projectBuildDirectory
            nuxtPage.file = path.join(basePath, componentFilePathFromJson+"");
        }

        if(!nuxtPage.file)
            throw new Error("Nuxt page with id "+nuxtPage.name +" has no defined file path")

        if(!nuxtPage.meta) {
            nuxtPage.meta = {}
        }

        nuxtPage.meta.page = jsonPage
        resultPages.push(nuxtPage)
    }
    return resultPages
}

export default {
    loadJsonConfig(projectBuildDirectory: string):FrontendProjectConfiguration {
        const jsonFilePath = path.resolve(projectBuildDirectory, 'project-configuration.json')
        if (!fs.existsSync(jsonFilePath)) {
            throw new Error(`project-configuration not found at ${jsonFilePath}`)
        }
        return (<FrontendProjectConfiguration> JSON.parse(fs.readFileSync(jsonFilePath, 'utf8')))
    },
    configureNuxtPages(pages: NuxtPage[], jsonPages:PageJson[], coreBuildDirectory: string, projectBuildDirectory: string):void {

        const newNuxtPages:NuxtPage[] = buildNewNuxtPages(pages, jsonPages, coreBuildDirectory, projectBuildDirectory);
        pages.splice(0,pages.length)
        pages.push(...newNuxtPages)
    }
}