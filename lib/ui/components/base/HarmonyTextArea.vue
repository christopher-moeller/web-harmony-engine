<template>
  <div class="layout">
    <label :class="!!errorMessages ? ['danger-label-style'] : []">{{caption}}</label>
    <textarea
        :rows="rows"
        :cols="cols"
        v-model="computedModel"
        :class="!!errorMessages ? ['danger-input-style'] : []"
        :readonly="isReadonly"
        style="resize: none;"
    >

    </textarea>
    <p class="error-message" v-for="(error, index) in errorMessages" :key="index">{{error}}</p>
  </div>
</template>

<script lang="ts" setup>
import {ComputedUtils} from "~/utils/ComputedUtils";

const props = defineProps({
  caption: {
    type: String,
    required: true
  },
  rows: {
    type: Number,
    required: false,
    default: 4
  },
  cols: {
    type: Number,
    required: false,
    default: 50
  },
  modelValue: {
    type: String,
    required: false
  },
  errorMessages: {
    type: Array,
    required: false
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  }
})

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

</script>

<style scoped>

.layout {
  position: relative;
  margin-top: 15px;
  margin-bottom: 15px;
}

.layout label {
  font-size: 14px;
  margin-bottom: 5px;
  margin-left: 25px;
  font-weight: 500;
  color: var(--harmony-black);
  width: 100%;
  display: block;
  user-select: none;
}

.layout textarea {
  width: 100%;
  border-radius: 20px;
  box-shadow: none;
  border: 1px solid var(--harmony-light-3);
  font-size: 18px;
  padding: 5px 20px;
  background: none;
  color: var(--harmony-black);
}

.error-message {
  color: var(--harmony-danger);
  font-size: 12px;
  margin-top: 2px;
  margin-left: 25px;
}

.danger-input-style {
  border-color: var(--harmony-danger) !important;
  color: var(--harmony-danger) !important;
}

.danger-label-style {
  color: var(--harmony-danger) !important;
}
</style>