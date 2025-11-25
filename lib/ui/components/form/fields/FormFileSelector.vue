<template>
  <form-field-wrapper :field-context="fieldContext" v-slot="slotProps" @fieldLoaded="onFieldLoaded">
    <harmony-file-selector
        :caption="props.caption"
        v-model="fieldContext.fieldComputed.value"
        :is-readonly="isReadOnlyField"
        :error-messages="slotProps['errorMessages']"
    />
  </form-field-wrapper>
</template>

<script lang="ts" setup>

import {getCurrentInstance} from "vue";
import HarmonyFileSelector from "@core/components/base/HarmonyFileSelector.vue";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";


const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
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