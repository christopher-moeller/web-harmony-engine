<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="label" resource-name="userRoles">
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()" />
    <form-select-field field-path="includedRights" :caption="i18n.translate('Rights').build()" />
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";

const i18n = I18N.of("userRoles[id]")

</script>

<style scoped>

</style>