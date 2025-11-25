import {PluginOption} from "vite";
import path from "path";
import fs from "fs";

interface PluginResolver {
    getViteCustomImportPathPlugin(coreBuildDirectory: string, projectBuildDirectory: string): PluginOption
}

interface ComponentReplacement {
    coreImport: string,
    projectImport: string
}

function isCoreComponent(importPath: string, coreBuildDir: string): boolean {
    return importPath.startsWith(coreBuildDir)
}

function loadComponentReplacements(projectBuildDirectory: string): ComponentReplacement[] {
    const jsonFilePath = path.resolve(projectBuildDirectory, 'import-replacements.json')
    if (!fs.existsSync(jsonFilePath)) {
        console.log("No replacement file under "+jsonFilePath+" found!")
        return []
    }
    const data = <ComponentReplacement[]> JSON.parse(fs.readFileSync(jsonFilePath, 'utf8'))
    console.log("Found "+data.length+" components which will be replaced by components of the project")
    return data
}

const pluginResolver:PluginResolver = {
    getViteCustomImportPathPlugin(coreBuildDirectory: string, projectBuildDirectory: string): PluginOption {
        const importReplacements:ComponentReplacement[] = loadComponentReplacements(projectBuildDirectory)
        const resolveImportFunction = importReplacements.length > 0 ?
            (importPath:string) => {
                if(isCoreComponent(importPath, coreBuildDirectory)) {
                    const relativeImportPath = importPath.replace(coreBuildDirectory, "")
                    const replacementEntry = importReplacements.find(e => e.coreImport === relativeImportPath)
                    if(replacementEntry) {
                        return path.join(projectBuildDirectory, replacementEntry.projectImport)
                    }
                }
            } :
            () => {}
        return {
            name: "custom-import-paths",
            enforce: "pre",
            resolveId: resolveImportFunction
        }
    }
}

export default pluginResolver