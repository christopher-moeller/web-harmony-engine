<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="uniqueName" resource-name="actors">
    <form-text-field field-path="uniqueName" :caption="i18n.translate('Label').build()"/>
    <form-text-field field-path="type" :caption="i18n.translate('Type').build()"/>
    <form-text-field field-path="userId" :caption="i18n.translate('User ID').build()"/>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";

const i18n = I18N.of("actors[id]")

</script>

<style scoped>

</style>