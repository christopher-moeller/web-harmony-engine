<template>
  <GenericResourceDetailView :resource-context="resourceContext" breadcrumb-label-v-m-attribute="toEmail" resource-name="emails">
    <template v-slot:default>
      <div style="display: flex">
        <div>
          <form-text-field field-path="createdAt" :caption="i18n.translate('Created At').build()"/>
        </div>
        <div style="margin-left: 30px">
          <form-text-field field-path="lastSending" :caption="i18n.translate('Last sending').build()"/>
        </div>
      </div>

      <form-text-field field-path="fromEmail" :caption="i18n.translate('From').build()"/>
      <form-text-field field-path="toEmail" :caption="i18n.translate('To').build()"/>
      <form-text-field field-path="subject" :caption="i18n.translate('Subject').build()"/>
      <form-text-area field-path="htmlMessage" :caption="i18n.translate('HTML Message').build()"/>
    </template>
    <template v-slot:additionalMenuItems>
      <harmony-three-dots-menu-item :caption="i18n.translate('Preview').build()" @onItemClick="onOpenPreview"/>
      <harmony-three-dots-menu-item :caption="i18n.translate('Send').build()" @onItemClick="onSendClick" :is-in-loading-mode="isCurrentlySending"/>
    </template>
  </GenericResourceDetailView>
  <harmony-modal v-model="showPreviewModal">
    <div v-html="htmlMessageForPreview">
    </div>
  </harmony-modal>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import HarmonyThreeDotsMenuItem from "@core/components/base/HarmonyThreeDotsMenuItem.vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import {ref} from "vue";
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import useCoreApi from "~/composables/useCoreApi";

const i18n = I18N.of("emails[id]")

const showPreviewModal = ref(false)

const resourceContext = useResourceContext("emails", useRouterUtils().getResourceIdByUrl())
const htmlMessageForPreview = resourceContext.getFieldValueRef("htmlMessage")

const onOpenPreview = () => {
  showPreviewModal.value = true
}

const isCurrentlySending = ref(false)
const coreApi = useCoreApi()
const onSendClick = async () => {
  const resourceId = resourceContext.getResourceId()
  await coreApi.api().getEmailApi().sendEmail(resourceId)
  isCurrentlySending.value = false
}


</script>

<style scoped>


</style>