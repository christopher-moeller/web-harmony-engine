<template>
  <template v-if="!javaType">
    <!-- show nothing currently -->
  </template>
  <template v-else-if="javaType === 'java.lang.Boolean'">
    <form-checkbox :caption="label" :field-path="fieldPath"/>
  </template>
  <template v-else-if="javaType === 'java.lang.Enum'">
    <form-select-field :caption="label" :field-path="fieldPath" :defined-options="definedOptions" :single-selection="true"/>
  </template>
  <template v-else-if="javaType === 'java.lang.Long' || javaType === 'java.lang.Integer'">
    <form-text-field :caption="label" :field-path="fieldPath" :is-number="true"/>
  </template>
  <template v-else>
    <form-text-field :caption="label" :field-path="fieldPath"/>
  </template>
</template>

<script lang="ts" setup>
import FormCheckbox from "~/components/form/fields/FormCheckbox.vue";
import FormSelectField from "~/components/form/fields/FormSelectField.vue";
import FormTextField from "~/components/form/fields/FormTextField.vue";

const props = defineProps({
  fieldPath: {
    type: String,
    required: true
  },
  label: {
    type: String,
    required: false
  },
  javaType: {
    type: String,
    required: false
  },
  definedOptions: {
    type: Array,
    required: false,
    default: []
  }
})
</script>

<style scoped>

</style>