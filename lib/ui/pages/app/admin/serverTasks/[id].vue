<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="taskName"
      resource-name="serverTasks"
      :resource-context="resourceContext"
  >
    <template #additionalMenuItems>
      <HarmonyThreeDotsMenuItem :caption="i18n.translate('Execute').build()" @onItemClick="onExecuteClick" :is-in-loading-mode="isCurrentlyExecuting"/>
    </template>
    <template #default>
      <form-text-field field-path="taskId" :caption="i18n.translate('ID').build()"/>
      <form-text-field field-path="taskName" :caption="i18n.translate('taskName').build()"/>
      <form-text-field field-path="lastExecution" :caption="i18n.translate('Last Execution').build()"/>
      <form-checkbox field-path="isRequired" caption="Is Required"/>
    </template>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "~/utils/I18N";
import {ref} from "vue";
import HarmonyThreeDotsMenuItem from "~/components/base/HarmonyThreeDotsMenuItem.vue";
import {useNotificationStore} from "~/store/notificationStore";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";
import useHarmonyCookies from "~/composables/useHarmonyCookies";

const i18n = I18N.of("serverTasks[id]")

const isCurrentlyExecuting = ref(false)
const notificationStore = useNotificationStore();

const resourceContext = useResourceContext("serverTasks", useRouterUtils().getResourceIdByUrl())

const coreApi = useCoreApi()
const cookieResolver = useHarmonyCookies()
const onExecuteClick = async () => {
  const resourceId = resourceContext.getResourceId()
  isCurrentlyExecuting.value = true
  await coreApi.api().getServerTaskApi().executeByUUID(resourceId)
  isCurrentlyExecuting.value = false
  await resourceContext.hardFetchApiState()
  await notificationStore.init(cookieResolver)
}

</script>

<style scoped>


</style>