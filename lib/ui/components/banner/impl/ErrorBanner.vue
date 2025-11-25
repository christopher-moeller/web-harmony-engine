<template>
  <div id="banner">
    <i class='bx bxs-error'></i>
    <p>{{internalMessage}}</p>
    <i v-if="apiError" class='bx bx-info-circle details-icon' @click="onShowDetails"></i>
  </div>
  <harmony-modal v-model="isModalOpen">
    <div class="detail-section">
      <h3>{{i18n.translate('Details').build()}}</h3>
      <div v-if="apiError">
        <p><b>Type </b>{{apiError.type}}</p>
        <p><b>Message </b>{{apiError.message}}</p>
        <p><b>Status </b>{{apiError.status}}</p>
        <p><b>Status Text </b>{{apiError.statusText}}</p>
      </div>
    </div>
    <div class="detail-section">
      <h3>{{i18n.translate('Java Stacktrace').build()}}</h3>
      <div v-if="javaStacktrace">
        <p><b>Message </b>{{javaStacktrace.message}}</p>
        <p><b>Stacktrace</b></p>
        {{javaStacktrace.stackTraceText}}
      </div>
    </div>
  </harmony-modal>
</template>

<script setup lang="ts">

import {PropType} from "vue";
import {Banner} from "~/store/bannerStore";
import {HarmonyAxiosResponse, HarmonyErrorBannerData, HarmonyErrorBannerType} from "~/utils/HarmonyTypes";
import useAuthenticationContext from "~/composables/useAuthenticationContext";
import {ApiError, ApiErrorWithJavaExceptionInstance, ApiJavaExceptionInstance, ECoreActorRight} from "~/CoreApi";
import HarmonyModal from "~/components/base/HarmonyModal.vue";
import useBannerContext from "~/composables/useBannerContext";

const i18n = I18N.of("ErrorBanner")

const props = defineProps({
  banner: {
    type: Object as PropType<Banner>,
    required: true
  }
})

const isModalOpen = ref(false)

const authenticationContext = useAuthenticationContext()
const userIsAuthenticated = await authenticationContext.getIsAuthenticated()
const userHasRightToSeeDetailMessage = userIsAuthenticated && await authenticationContext.hasRight(ECoreActorRight.CORE_ERROR_MESSAGE_DETAIL_READ)
const userHasRightToSeeJavaStacktrace = userIsAuthenticated && await useAuthenticationContext().hasRight(ECoreActorRight.CORE_ERROR_JAVA_STACKTRACE_READ)

const internalMessage = ref(userHasRightToSeeDetailMessage ? props.banner!.message : i18n.translate("Error").build())

const apiError = ref<ApiError>()
const javaStacktrace = ref<ApiJavaExceptionInstance>()

const errorData:HarmonyErrorBannerData = props.banner!.additionalData!
if(errorData.type === HarmonyErrorBannerType.SERVER_ERROR && userHasRightToSeeDetailMessage) {
  const serverResponse:HarmonyAxiosResponse = errorData.data
  const apiErrorData:ApiError = serverResponse.data
  apiError.value = apiErrorData
  if(apiErrorData.message) {
    internalMessage.value = apiErrorData.message
  }
  if(Object.hasOwn(apiErrorData, "javaException") && userHasRightToSeeJavaStacktrace) {
    const apiErrorWithJavaException:ApiErrorWithJavaExceptionInstance = <ApiErrorWithJavaExceptionInstance> apiErrorData
    javaStacktrace.value = apiErrorWithJavaException.javaException
  }
}

const bannerContext = useBannerContext()

const onShowDetails = () => {
  isModalOpen.value = true
  bannerContext.getActiveBannerByUUID(props.banner!.uuid)!.autoCloseInSeconds = undefined
}

watch(isModalOpen, value => {
  if(!value) {
    bannerContext.removeBanner(props.banner!.uuid)
  }
})

</script>

<style scoped>

#banner {
  background-color: var(--harmony-danger);
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  color: var(--harmony-light-1);
  padding: 0.5em 1em;
  max-width: 90vw;
}

#banner p {
  margin-left: 1em;
  font-size: 16px;
  color: var(--harmony-light-1);
  word-break: break-word;
}

.details-icon {
  cursor: pointer;
  margin-left: 0.5em;
}

.detail-section {
  margin-top: 1em
}

</style>