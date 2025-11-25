<template>
  <GenericResourceDetailView breadcrumb-label-v-m-attribute="name" resource-name="secureKeys">
    <template v-slot:default>
      <form-text-field field-path="name" :caption="i18n.translate('Name').build()"/>
      <form-text-field field-path="key" :caption="i18n.translate('Key').build()" :is-password="true"/>
    </template>
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";

const i18n = I18N.of("secureKeys[id]")


</script>

<style scoped>


</style>