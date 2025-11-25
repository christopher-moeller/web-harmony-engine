export default function useLayoutState() {


    const windowSize = ref({
        width: 0,
        height: 0,
        state: "PC"
    })

    const updateWindowSize = () => {
        windowSize.value.width = window.innerWidth
        windowSize.value.height = window.innerHeight

        if(windowSize.value.width > 500) {
            windowSize.value.state = "PC"
        } else {
            windowSize.value.state = "MOBILE"
        }
    }

    if(process.client) {
        updateWindowSize()
        onMounted(() => {
            window.addEventListener('resize', updateWindowSize, true);
        })
    } else if(process.server) {
        const nuxtApp = useNuxtApp()
        const headers = nuxtApp.ssrContext?.event.node.req.headers

        const userAgent = headers?.["user-agent"]

        if(userAgent) {
            const mobileDevices = ["Android","webOS","iPhone","iPad","iPod","BlackBerry","IEMobile","Opera Mini"]
            mobileDevices.forEach(deviceName => {
                if(mobileDevices.includes(deviceName)) {
                    windowSize.value.state = "MOBILE"
                }
            })
        }
    }

    onUnmounted(() => {
        window.removeEventListener('resize', updateWindowSize)
    })
    

    return {
        windowSize
    };
  }