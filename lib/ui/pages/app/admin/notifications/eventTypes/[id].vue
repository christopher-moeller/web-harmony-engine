<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="label" resource-name="notificationEventTypes">
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-checkbox field-path="isActive" :caption="i18n.translate('Is Active').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";

const i18n = I18N.of("notificationEventTypes[id]")

</script>

<style scoped>


</style>