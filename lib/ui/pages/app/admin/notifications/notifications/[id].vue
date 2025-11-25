<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="id" resource-name="notifications">
    <form-text-field  field-path="caption" :caption="i18n.translate('Caption').build()"/>
    <form-text-field  field-path="sendAt" :caption="i18n.translate('Send At').build()"/>
    <form-text-field  field-path="recipient" :caption="i18n.translate('Recipient').build()"/>
    <form-text-area field-path="textMessage" :caption="i18n.translate('Text Message').build()"/>
    <form-checkbox field-path="read" :caption="i18n.translate('Read').build()"/>
    <form-text-area field-path="payload" :caption="i18n.translate('Payload').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";

const i18n = I18N.of("notifications[id]")

</script>

<style scoped>


</style>