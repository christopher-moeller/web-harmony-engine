<template>
  <GenericResourceDetailView ref="resourceView" breadcrumb-label-v-m-attribute="id" resource-name="reportUserErrors">
    <form-text-field field-path="page" :caption="i18n.translate('Page').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-file-selector field-path="attachments" :caption="i18n.translate('Attachments').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormFileSelector from "~/components/form/fields/FormFileSelector.vue";

const i18n = I18N.of("reportUserErrors[id]")



</script>

<style scoped>

</style>