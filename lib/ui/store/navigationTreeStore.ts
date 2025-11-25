import {defineStore} from "pinia";
import {NavigationTreeJson} from "@core/CoreApi";

interface State {
    navigationTree?: NavigationTreeJson
}

export const useNavigationTreeStore = defineStore('navigationTreeStore', {
    state: (): State => ({ navigationTree: undefined }),
    getters: {
        getNavigationTree: (state): NavigationTreeJson | undefined => state.navigationTree
    },
    actions: {
        setNavigationTree(navigationTreeJson: NavigationTreeJson) {
            this.$state.navigationTree = navigationTreeJson
        }
    },
})