<template>
  <button class="button" :class="dynamicBtnClass" @click="onButtonClicked">
    <span v-show="isInLoadingMode" class="loader"></span>
    {{caption}}
  </button>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";

const props = defineProps({
  caption: {
    type: String,
    required: true,
    default: "OK"
  },
  type: {
    type: String,
    required: false,
    default: HarmonyButtonType.PRIMARY
  },
  disabled: {
    type: Boolean,
    required: false,
    default: false
  },
  isInLoadingMode: {
    type: Boolean,
    required: false,
    default: false
  }
})

const emit = defineEmits<{
  (e: 'click'): void,
  (e: 'update:modelValue'): void
}>()


const onButtonClicked = () => {
  if(!props.disabled) {
    emit("click")
  }
}

const dynamicBtnClass = computed(() => {
  if(props.disabled || props.isInLoadingMode)
    return "disabled"

  return props.type
})

</script>

<style scoped>

.button {
  border: none;
  color: var(--harmony-light-1);
  padding: 10px 20px;
  text-align: center;
  text-decoration: none;
  display: inline-block;
  cursor: pointer;
  border-radius: 100px;
  height: 3em;
}

.primary {
  background-color: var(--harmony-primary);
}

.secondary {
  background-color: var(--harmony-light-2);
  border-color: var(--harmony-black);
  border-style: solid;
  color: var(--harmony-black);
}

.danger {
  background-color: var(--harmony-light-1);
  border-color: var(--harmony-danger);
  border-style: solid;
  color: var(--harmony-danger);
}

.disabled {
  background-color: var(--harmony-light-3);
  cursor: not-allowed;
}

.loader {
  width: 10px;
  height: 10px;
  border: 1px solid #FFF;
  border-bottom-color: transparent;
  border-radius: 50%;
  display: inline-block;
  box-sizing: border-box;
  animation: rotation 1s linear infinite;
  margin-right: 10px;
}

@keyframes rotation {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

</style>