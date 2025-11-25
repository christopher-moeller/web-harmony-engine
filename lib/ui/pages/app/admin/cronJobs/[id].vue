<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="label"
      resource-name="cronJobs"
      :resource-context="resourceContext"
  >
    <template v-slot:default>
      <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
      <form-checkbox caption="Is Activated" field-path="isActivated"/>
      <form-checkbox field-path="isCurrentlyRunning" :caption="i18n.translate('Is currently running').build()"/>
      <form-text-field field-path="lastExecutedAt" :caption="i18n.translate('Last executed at').build()"/>
      <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
      <form-text-field field-path="javaClass" :caption="i18n.translate('Java Class').build()"/>
      <form-text-field field-path="cronTrigger" :caption="i18n.translate('Cron Trigger').build()"/>
    </template>

    <template v-slot:additionalMenuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Run').build()" @onItemClick="onRunClick" :is-in-loading-mode="isCurrentlyRunning"/>
    </template>

  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("cronJobs[id]")

const resourceContext = useResourceContext("cronJobs", useRouterUtils().getResourceIdByUrl())

const isCurrentlyRunning = ref(false)
const description = resourceContext.getFieldValueRef("description")

const coreApi = useCoreApi()
const onRunClick = async () => {
  const resourceId = resourceContext.getResourceId()
  await coreApi.api().getCronJobApi().execute(resourceId)
  await resourceContext.hardFetchApiState()
}

</script>

<style scoped>


</style>