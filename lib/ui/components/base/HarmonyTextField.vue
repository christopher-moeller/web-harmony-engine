<template>
  <div class="card-input">
    <label class="card-input__label" :class="!!errorMessages ? ['danger-label-style'] : []">{{caption}}</label>
    <i v-if="internalInnerIcon" :class="'bx ' + internalInnerIcon" style="cursor: pointer" @click="onIconClick"></i>
    <input :type="formFieldType"
           :readonly="isReadonly"
           :autocomplete="autocompleteName"
           v-model="computedModel"
           :style="internalInnerIcon ? 'padding-left: 60px' : ''"
           class="card-input__input"
           :class="!!errorMessages ? ['danger-input-style'] : []"
           :placeholder="placeholder"
    >
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
  modelValue: {
    required: false
  },
  errorMessages: {
    type: Array,
    required: false
  },
  autocompleteName: {
    type: String,
    required: false
  },
  innerIcon: {
    type: String,
    required: false
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  },
  placeholder: {
    type: String,
    required: false
  }
})

const isPasswordVisible = ref(false)
const formFieldType = computed(() => props.isPassword && !isPasswordVisible.value ? "password" : props.isNumber ? "number" : "text")
const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)
const internalInnerIcon = computed(() => props.isPassword ? "bx-hide eye-icon" : props.innerIcon)

const onIconClick = () => {
  if(props.isPassword && computedModel.value) {
    isPasswordVisible.value = !isPasswordVisible.value
  }
}
</script>

<style scoped>
.card-input {
  position: relative;
  margin-top: 15px;
  margin-bottom: 15px;
}

.card-input label {
  font-size: 14px;
  margin-bottom: 5px;
  margin-left: 25px;
  font-weight: 500;
  color: var(--harmony-black);
  width: 100%;
  display: block;
  user-select: none;
}

.card-input input {
  width: 100%;
  height: 50px;
  border-radius: 100px;
  box-shadow: none;
  border: 1px solid var(--harmony-light-3);
  font-size: 18px;
  padding: 5px 20px;
  background: none;
  color: var(--harmony-black);
}

.danger-input-style {
  border-color: var(--harmony-danger) !important;
  color: var(--harmony-danger) !important;
}

.danger-label-style {
  color: var(--harmony-danger) !important;
}

.error-message {
  color: var(--harmony-danger);
  font-size: 12px;
  margin-top: 2px;
  margin-left: 25px;
}

i {
  position: absolute;
  margin-left: 20px;
  margin-top: 10px;
}
</style>