<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="startedAt" resource-name="startupLogs">
    <form-text-field field-path="startedAt" :caption="i18n.translate('Started At').build()"/>
    <form-text-field field-path="durationInMillis" :caption="i18n.translate('Duration Milliseconds').build()"/>
    <form-text-field field-path="durationText" :caption="i18n.translate('Duration').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";

const i18n = I18N.of("startupLogs[id]")



</script>

<style scoped>

</style>