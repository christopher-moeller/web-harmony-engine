<template>
  <GenericResourceDetailView
      ref="detailView"
      breadcrumb-label-v-m-attribute="label"
      resource-name="registryItems"
      :resource-context="resourceContext"
  >
    <form-text-field field-path="label" :caption="i18n.translate('Label').build()"/>
    <form-text-area field-path="description" :caption="i18n.translate('Description').build()"/>
    <form-java-type-field
        :label="i18n.translate('Value').build()"
        fieldPath="value"
        :javaType="javaSpecificationType"
        :definedOptions="definedSelectableOptions"
    />
  </GenericResourceDetailView>
</template>

<script lang="ts" setup>

import GenericResourceDetailView from "~/components/view/GenericResourceDetailView.vue"
import I18N from "@core/utils/I18N";
import FormTextField from "~/components/form/fields/FormTextField.vue";
import FormTextArea from "~/components/form/fields/FormTextArea.vue";
import FormJavaTypeField from "~/components/form/fields/FormJavaTypeField.vue";
import useResourceContext from "~/composables/form/useResourceContext";
import useRouterUtils from "~/composables/useRouterUtils";

const i18n = I18N.of("registryItems[id]")

const detailView = ref()

const resourceContext = useResourceContext("registryItems", useRouterUtils().getResourceIdByUrl())
const javaSpecificationType = resourceContext.getFieldValueRef("javaSpecificationType")
const definedSelectableOptions = resourceContext.getFieldValueRef("definedSelectableOptions")


</script>

<style scoped>


</style>