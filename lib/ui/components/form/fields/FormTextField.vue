<template>
  <form-field-wrapper
      :field-context="fieldContext"
      v-slot="slotProps"
      @fieldLoaded="onFieldLoaded"
      :fields-are-equal-check="fieldsAreEqualCheck"
  >
    <harmony-text-field
        :caption="props.caption"
        v-model="fieldContext.fieldComputed.value"
        :is-password="props.isPassword"
        :errorMessages="slotProps['errorMessages']"
        :autocompleteName="autocompleteName"
        :is-readonly="isReadOnlyField"
        :is-number="isNumber"
    />
  </form-field-wrapper>
</template>

<script lang="ts" setup>

import HarmonyTextField from "@core/components/base/HarmonyTextField.vue"
import {getCurrentInstance} from "vue";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import {CustomFormFieldIsEqualCheck, RegisteredViewModelField} from "~/utils/HarmonyTypes";


const emit = defineEmits<{
  (e: 'onEnterPressed', responseData: any): void
}>()

const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  caption: {
    type: String,
    required: true
  },
  isPassword: {
    type: Boolean,
    required: false,
    default: false
  },
  isNumber: {
    type: Boolean,
    required: false,
    default: false
  },
  autocompleteName: {
    type: String,
    required: false
  }
})

const fieldContext = useFormFieldContext(getCurrentInstance())

const fieldsAreEqualCheck:CustomFormFieldIsEqualCheck = {
  isEqual(field1: string, field2: string): boolean {
    const s1 = field1 ? (field1 == '' ? null : field1) : null
    const s2 = field2 ? (field2 == '' ? null : field2) : null
    return s1 == s2
  }
}

const isReadOnlyField = ref(true)
const onFieldLoaded = (field: RegisteredViewModelField) => {
  const inputSpecification = field.schema.validInputSpecification
  isReadOnlyField.value = !!inputSpecification!.isReadOnly
}


</script>

<style scoped>

</style>