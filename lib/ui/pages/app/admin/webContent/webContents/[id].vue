<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="label"
      resource-name="webContents"
      :resource-context="resourceContext"
  >
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-select-field field-path="area" :caption="i18n.translate('Area').build()" :singleSelection="true" />
    <form-web-content-editor caption="Test" field-path="content" :web-content-id="resourceContext.getResourceId()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";
import FormWebContentEditor from "~/components/form/fields/utils/FormWebContentEditor.vue";

const i18n = I18N.of("webContents[id]")

const resourceContext = useResourceContext("webContents", useRouterUtils().getResourceIdByUrl())
const content = resourceContext.getFieldValueRef("content")


</script>

<style scoped>


</style>