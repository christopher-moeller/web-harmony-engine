<template>
  <p v-if="caption" style="margin-bottom: 2em">{{caption}}</p>
  <form-field-wrapper :field-context="fieldContext" v-slot="slotProps" @fieldLoaded="onFieldLoaded">
    <template v-for="(element, index) in fieldContext.fieldComputed.value">
      <form-context :path="fieldPath" :arrayIndex="index">
        <slot :index="index" :element="element"></slot>
      </form-context>
    </template>
  </form-field-wrapper>
</template>

<script lang="ts" setup>
import {getCurrentInstance} from "@vue/runtime-core";
import useFormFieldContext from "~/composables/form/useFormFieldContext";
import FormFieldWrapper from "~/components/form/fields/FormFieldWrapper.vue";
import FormContext from "~/components/form/fields/FormContext.vue";
import {RegisteredViewModelField} from "~/utils/HarmonyTypes";

const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  caption: {
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