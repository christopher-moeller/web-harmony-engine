<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="id"
      resource-name="harmonyEvents"
      :resource-context="resourceContext"
  >
    <template v-slot:default>
      <div style="display: flex">
        <div style="width: 100%">
          <form-text-field field-path="createdAt" :caption="i18n.translate('Created At').build()"/>
        </div>
        <div style="margin-left: 30px; width: 100%">
          <form-text-field field-path="createdBy" :caption="i18n.translate('Created By').build()"/>
        </div>
      </div>

      <form-text-field field-path="javaType" :caption="i18n.translate('Java Type').build()"/>

      <p v-if="payloadAsJson" class="json-viewer-area">{{payloadAsJson}}</p>
    </template>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";

const i18n = I18N.of("harmonyEvents[id]")

const resourceContext = useResourceContext("harmonyEvents", useRouterUtils().getResourceIdByUrl())

const rawPayload = resourceContext.getFieldValueRef("payload")
const payloadAsJson = computed(() => rawPayload.value ? JSON.parse(rawPayload.value) : rawPayload)


</script>

<style scoped>

.json-viewer-area {
  background-color: var(--harmony-light-1) !important;
}

</style>