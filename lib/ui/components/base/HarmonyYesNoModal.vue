<template>
  <harmony-modal v-model="computedModel" :is-minimal="true">
    <div class="message-wrapper">
      <p>{{message}}</p>
    </div>
    <div class="btn-wrapper">
      <harmony-button :caption="i18n.translate('Yes').build()" @click="yesClickInternal"/>
      <harmony-button :caption="i18n.translate('No').build()" @click="noClickInternal" :type="HarmonyButtonType.SECONDARY" style="margin-left: 10px"/>
    </div>
  </harmony-modal>
</template>

<script lang="ts" setup>
import {ComputedUtils} from "~/utils/ComputedUtils";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import I18N from "@core/utils/I18N";
import {HarmonyButtonType} from "~/utils/HarmonyTypes";

const emit = defineEmits<{
  (e: 'onYes'): void,
  (e: 'onNo'): void,
  (e: 'update:modelValue'): void
}>()

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  message: {
    type: String,
    required: true
  }
})
const i18n = I18N.of("HarmonyYesNoModal")

// @ts-ignore
const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const yesClickInternal = () => {
  computedModel.value = false
  emit("onYes")
}

const noClickInternal = () => {
  computedModel.value = false
  emit("onNo")
}
</script>

<style scoped>

.message-wrapper {
  margin-top: 80px;
  display: flex;
  justify-content: center;
}

.btn-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>