import {defineStore} from "pinia";
import {AuthenticatedUserDto} from "@core/CoreApi";


interface State {
    token?: string,
    user?: AuthenticatedUserDto
}

export const useAuthenticationStore = defineStore('authenticationState', {
    state: (): State => ({ token: undefined, user: undefined }),
    getters: {
        getUser: (state) => (token: string):AuthenticatedUserDto | undefined => {
            if(!state.user) {
                return undefined
            }
            if(state.token !== token) {
                throw new Error("Local user is not in sync with API state")
            }
            return state.user!
        }
    },
    actions: {
        setUser(token: string | null | undefined, user: AuthenticatedUserDto) {
            this.$state.token = token ? token : undefined
            this.$state.user = user
        }
    },
})