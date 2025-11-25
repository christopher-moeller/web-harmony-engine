<template>
  <div v-if="modelValue" class="modal-overlay" @click="closeModal">
      <HarmonyPanel class="harmony-panel" :class="dynamicClasses" @click.stop style="background-color: var(--harmony-light-2);" :caption="caption">
        <slot></slot>
      </HarmonyPanel>
  </div>
</template>

<script lang="ts" setup>

import {computed} from "vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";

const emit = defineEmits<{
  (e: 'update:modelValue'): void
}>()

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  isMinimal: {
    type: Boolean,
    required: false,
    default: false
  },
  caption: {
    type: String,
    required: false
  }
})
const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const closeModal = () => computedModel.value = false

const dynamicClasses = computed(() => props.isMinimal ? ["harmony-panel-minimal"] : [])

</script>

<style scoped>

.modal-overlay {
  display: block;
  position: fixed;
  z-index: 10000000 !important;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0,0,0); /* Fallback color */
  background-color: rgba(0,0,0,0.4); /* Black w/ opacity */
}

.harmony-panel {
  -ms-overflow-style: none;  /* IE and Edge */
  margin-top: 60px;
  height: calc(80vh);
  overflow-y: scroll;
}

/* Hide scrollbar for Chrome, Safari and Opera */
.harmony-panel::-webkit-scrollbar {
  display: none;
}

.harmony-panel-minimal {
  margin-top: 100px;
  height: calc(50vh);
}

@media screen and (min-width: 700px) {
  .harmony-panel {
    margin-left: 10%;
    margin-right: 10%;
  }

  .harmony-panel-minimal {
    margin-left: 20% !important;
    margin-right: 20% !important;
  }
}

</style>