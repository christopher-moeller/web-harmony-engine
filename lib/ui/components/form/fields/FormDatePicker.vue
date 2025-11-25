<template>
  <form-field-wrapper
      :field-context="fieldContext"
      v-slot="slotProps"
      @fieldLoaded="onFieldLoaded"
      :fields-are-equal-check="fieldsAreEqualCheck"
  >

    <harmony-datepicker
        :caption="props.caption"
        v-model="fieldContext.fieldComputed.value"
        :errorMessages="slotProps['errorMessages']"
        :is-readonly="isReadOnlyField"
    />
  </form-field-wrapper>
</template>

<script lang="ts" setup>

import {getCurrentInstance} from "vue";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import {CustomFormFieldIsEqualCheck, RegisteredViewModelField} from "~/utils/HarmonyTypes";
import HarmonyDatepicker from "~/components/base/HarmonyDatepicker.vue";


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

const fieldsAreEqualCheck:CustomFormFieldIsEqualCheck = {
  isEqual(field1: string, field2: string): boolean {
    const s1 = field1 ? (field1 == '' ? null : field1) : null
    const s2 = field2 ? (field2 == '' ? null : field2) : null
    return s1 == s2
  }
}

const fieldContext = useFormFieldContext(getCurrentInstance())

const isReadOnlyField = ref(true)
const onFieldLoaded = (field: RegisteredViewModelField) => {
  const inputSpecification = field.schema.validInputSpecification
  isReadOnlyField.value = !!inputSpecification!.isReadOnly
}


</script>

<style scoped>

</style>