<template>
  <form-field-wrapper :field-context="fieldContext" v-slot="slotProps" @fieldLoaded="onFieldLoaded">
    <div v-if="!fieldContext.fieldComputed.value">
      <p>Invalid Content</p>
    </div>
    <web-contend-editor-view v-else v-model="fieldContext.fieldComputed.value" :web-content-id="webContentId"/>
  </form-field-wrapper>
</template>

<script lang="ts" setup>

import {getCurrentInstance} from "vue";
import HarmonyFileSelector from "@core/components/base/HarmonyFileSelector.vue";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";
import WebContendEditorView from "~/components/webcontent/views/WebContendEditorView.vue";


const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
  },
  webContentId: {
    type: String,
    required: false
  }
})

const fieldContext = useFormFieldContext(getCurrentInstance())

const isReadOnlyField = ref(true)
const onFieldLoaded = (field: RegisteredViewModelField) => {
  const inputSpecification = field.schema.validInputSpecification
  isReadOnlyField.value = !!inputSpecification!.isReadOnly
}


</script>

<style scoped>

</style>