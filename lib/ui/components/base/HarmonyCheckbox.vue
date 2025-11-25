<template>
  <div id="root-wrapper">
    <label v-if="!isReadonly" class="container">
      <input type="checkbox" v-model="computedModel">
      <span class="checkmark"></span>
    </label>
    <label v-else class="container">
      <span class="checkmark" :class="computedModel ? 'force-checked' : ''"></span>
    </label>
    <p id="caption" :class="!!errorMessages ? ['danger-label-style'] : []">{{caption}}</p>
  </div>
  <p class="error-message" v-for="(error, index) in errorMessages" :key="index">{{error}}</p>
</template>

<script lang="ts" setup>

import {ComputedUtils} from "~/utils/ComputedUtils";

const props = defineProps({
  caption: {
    type: String,
    required: false
  },
  modelValue: {
    type: Boolean,
    required: false
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  },
  errorMessages: {
    type: Array,
    required: false
  }
})
const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

</script>

<style scoped>
#root-wrapper {
  display: flex;
  margin: 0.5em 0;
}

#caption {
  margin-right: 15px;
}

.container {
  display: block;
  position: relative;
  padding-left: 35px;
  margin-bottom: 25px;
  cursor: pointer;
  font-size: 22px;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
}

/* Hide the browser's default checkbox */
.container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

/* Create a custom checkbox */
.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  height: 25px;
  width: 25px;
  background-color: var(--harmony-light-3);
  border-radius: 100%;
}

/* When the checkbox is checked, add a blue background */
.container input:checked ~ .checkmark {
  background-color: var(--harmony-primary);
}

/* Create the checkmark/indicator (hidden when not checked) */
.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

/* Show the checkmark when checked */
.container input:checked ~ .checkmark:after {
  display: block;
}

/* Style the checkmark/indicator */
.container .checkmark:after {
  left: 9px;
  top: 5px;
  width: 5px;
  height: 10px;
  border: solid var(--harmony-light-1);
  border-width: 0 3px 3px 0;
  -ms-transform: rotate(45deg);
  transform: rotate(45deg);
}


.force-checked {
  background-color: var(--harmony-primary);
}

.force-checked:after {
  display: block;
  left: 9px;
  top: 5px;
  width: 5px;
  height: 10px;
  border: solid var(--harmony-light-1);
  border-width: 0 3px 3px 0;
  -ms-transform: rotate(45deg);
  transform: rotate(45deg);
}

.danger-label-style {
  color: var(--harmony-danger) !important;
}

.error-message {
  color: var(--harmony-danger);
  font-size: 12px;
  margin-top: 2px;
}
</style>