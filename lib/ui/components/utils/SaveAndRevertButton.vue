<template>
  <div class="action-btn-row">
    <p v-if="isDirty" @click="onRevertChanges">{{i18n.translate('Revert changes').build()}}</p>
    <HarmonyButton icon-class="bxs-save" :caption="i18n.translate('Save').build()" :disabled="!isDirty" @click="onSave" :is-in-loading-mode="isCurrentlySaving"/>
  </div>
</template>

<script lang="ts" setup>

import I18N from "@core/utils/I18N";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";


const i18n = I18N.of("SaveAndRevertButton")

const props = defineProps({
  isDirty: {
    type: Boolean,
    required: true
  },
  isCurrentlySaving: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'onRevertChanges'): void,
  (e: 'onSave'): void
}>()

const onRevertChanges = () => {
  emit('onRevertChanges')
}

const onSave = () => {
  emit('onSave')
}

</script>

<style scoped>

.action-btn-row {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap-reverse;
  gap: 0.5em;
}

.action-btn-row p {
  margin-right: 10px;
  font-size: 14px;
  color: var(--harmony-light-3);
  cursor: pointer;
}

</style>