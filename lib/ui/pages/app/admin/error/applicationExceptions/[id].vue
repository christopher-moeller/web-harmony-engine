<template>
  <GenericResourceDetailView ref="resourceView" breadcrumb-label-v-m-attribute="exceptionType" resource-name="applicationExceptions">
    <form-text-field field-path="exceptionType" :caption="i18n.translate('Exception Type').build()"/>
    <form-text-field field-path="message" :caption="i18n.translate('Message').build()"/>
    <form-text-field field-path="timestamp" :caption="i18n.translate('Timestamp').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-text-area field-path="stacktrace" :caption="i18n.translate('Stacktrace').build()"/>
    <form-text-area field-path="log" :caption="i18n.translate('Log').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";

const i18n = I18N.of("applicationExceptions[id]")



</script>

<style scoped>

</style>