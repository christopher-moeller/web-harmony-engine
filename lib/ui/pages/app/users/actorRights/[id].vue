<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="label" resource-name="actorRights">
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <div class="checkbox-area">
      <form-checkbox field-path="isAllowedForSystemActor" :caption="i18n.translate('Is allowed for System Actor').build()"/>
      <form-checkbox field-path="isAllowedForUnknownPublicActor" :caption="i18n.translate('Is allowed for unknown public actor').build()"/>
    </div>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";

const i18n = I18N.of("actorRights[id]")

</script>

<style scoped>

.checkbox-area {
  display: flex;
  flex-wrap: wrap;
}

</style>