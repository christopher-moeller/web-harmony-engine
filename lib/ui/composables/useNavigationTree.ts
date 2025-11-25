import {NavigationItemJson, NavigationTreeJson} from "@core/CoreApi";
import {useNavigationTreeStore} from "@core/store/navigationTreeStore";
import {BreadcrumbItem} from "~/utils/HarmonyTypes";

function buildItemPathToPageIdRecursive(items: NavigationItemJson[], pageId: string): NavigationItemJson[] | undefined{
    for (let item of items) {
        if(item.pageId === pageId) {
            return [item];
        } else {
            if(item.children) {
                const foundPath = buildItemPathToPageIdRecursive(item.children, pageId)
                if(foundPath) {
                    return [item, ...foundPath]
                }
            }
        }
    }
    return undefined;
}

export default function () {
    return {
        getTree():NavigationTreeJson {
            const store = useNavigationTreeStore()
            if(!store.getNavigationTree) {
                store.setNavigationTree(<NavigationTreeJson> process.env.NAVIGATION_TREE)
            }

            return store.getNavigationTree!
        },
        findTreeItemPathByPageId(pageId: string): NavigationItemJson[] | undefined {
          return buildItemPathToPageIdRecursive(this.getTree().items!, pageId)
        },
        findTreeItemByPageId(pageId: string): NavigationItemJson | undefined {
            const itemPath:NavigationItemJson[] | undefined =  buildItemPathToPageIdRecursive(this.getTree().items!, pageId);
            if(itemPath) {
                return itemPath[itemPath.length - 1]
            } else {
                return undefined;
            }
        },
        buildGenericBreadcrumbItemsByPageId(pageId: string): BreadcrumbItem[] {
            const path = this.findTreeItemPathByPageId(pageId)
            if(path) {
                return this.buildGenericBreadcrumbItemsByPath(path);
            } else {
                return []
            }
        },
        buildGenericBreadcrumbItemsByPath(itemPath: NavigationItemJson[]): BreadcrumbItem[] {
            return itemPath.map(item => {
                return {
                    label: I18N.staticTranslateRouterPagesJsonLabel(item.englishLabel+"").build(),
                    link: item.link+""
                }
            });
        }
    }
}

