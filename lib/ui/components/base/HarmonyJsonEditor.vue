<template>
  <div class="json-editor">
    <textarea
        v-model="jsonText"
        :placeholder="i18n.translate('Enter JSON here...').build()"
        class="json-textarea"
    ></textarea>
    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-if="!error" class="success-message">{{i18n.translate('Valid JSON').build()}}</div>
  </div>
</template>

<script setup lang="ts">
import {ref} from 'vue';
import {WritableComputedRef} from "@vue/reactivity";
import {ComputedUtils} from "~/utils/ComputedUtils";

const i18n = I18N.of("HarmonyJsonEditor");

const props = defineProps({
  modelValue: {
    type: Object,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const computedModel:WritableComputedRef<any> = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const isJsonValid = (jsonString:string):boolean => {
  try {
    JSON.parse(jsonString);
    return true
  } catch (e) {
    return false
  }
};

const error = ref('');

const jsonText = computed({
  get: () => JSON.stringify(computedModel.value, null, "\t"),
  set: (jsonText:string) => {
    if(isJsonValid(jsonText)) {
      computedModel.value = JSON.parse(jsonText)
      error.value = undefined
    } else {
      error.value = i18n.translate("JSON not valid").build()
    }
  }
})


</script>

<style scoped>
.json-editor {
  display: flex;
  width: 100%;
  flex-direction: column;
  margin: 0 auto;
}

.json-textarea {
  width: 100%;
  height: 400px;
  padding: 10px;
  border: 1px solid var(--harmony-light-3);
  border-radius: 4px;
  font-family: monospace;
  font-size: 14px;
  resize: vertical;
}

.error-message {
  color: var(--harmony-danger);
  margin-top: 10px;
}

.success-message {
  color: var(--harmony-success);
  margin-top: 10px;
}
</style>
