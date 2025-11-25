<template>
  <GenericResourceDetailView
      breadcrumb-label-v-m-attribute="label"
      resource-name="webContentAreas"
  >
    <form-text-field  field-path="uniqueName" :caption="i18n.translate('Unique Name').build()" />
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-checkbox field-path="onlyOneContentInstanceAllowed" :caption="i18n.translate('Only one content instance allowed').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-select-field field-path="requiredReadRight" :caption="i18n.translate('Required Read Right').build()" :singleSelection="true" />
    <form-select-field field-path="requiredWriteRight" :caption="i18n.translate('Required Write Right').build()" :singleSelection="true" />
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";

const i18n = I18N.of("webContentAreas[id]")



</script>

<style scoped>


</style>