<template>
  <harmony-modal v-model="computedModel" :is-minimal="true">
    <div class="message-wrapper">
      <div>
        <div v-for="(column, index) in columns" style="margin: 10px; cursor: pointer" :key="index" class="header-cell-content">
          <span @click="onSortSelectionChanged(column.id)">{{column.caption}}</span>
          <template v-if="column.id === selectedSortColumnIdInternal">
            <span v-if="isSortAscInternal">&#8593;</span>
            <span v-else>&darr;</span>
          </template>
          <template v-else>
            <span class="sort-arrow-as-hint">&#8593;</span>
          </template>
        </div>
      </div>
    </div>
    <div class="btn-wrapper">
      <harmony-button :caption="i18n.translate('Apply').build()" @click="onApplyClick"/>
      <harmony-button :caption="i18n.translate('Cancel').build()" @click="onCancelClick" :type="HarmonyButtonType.SECONDARY" style="margin-left: 10px"/>
    </div>
  </harmony-modal>
</template>

<script lang="ts" setup>
import {PropType, ref, watch} from "vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import I18N from "@core/utils/I18N";
import {HarmonyButtonType, HarmonyTableColumn} from "~/utils/HarmonyTypes";

const emit = defineEmits<{
  (e: 'onApply', data: any): void,
  (e: 'update:modelValue'): void
}>()

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  selectedColumnId: {
    type: String,
    required: false
  },
  isAsc: {
    type: Boolean,
    required: true
  }
})

const i18n = I18N.of("HarmonyTableCardRepresentationSortingModal")

// @ts-ignore
const computedModel = ComputedUtils.createDelegatedComputedValue(props, context)

const selectedSortColumnIdInternal = ref<string | undefined>()
const isSortAscInternal = ref(true)

watch(computedModel, (v: boolean) => {
  if(v) {
    selectedSortColumnIdInternal.value = props.selectedColumnId
    isSortAscInternal.value = props.isAsc
  }
})

const onSortSelectionChanged = (columnId: string) => {
  if(selectedSortColumnIdInternal.value === columnId) {
    if(isSortAscInternal.value) {
      isSortAscInternal.value = false
    } else {
      isSortAscInternal.value = true
      selectedSortColumnIdInternal.value = undefined
    }
  } else {
    selectedSortColumnIdInternal.value = columnId
    isSortAscInternal.value = true
  }

}

const onApplyClick = () => {
  computedModel.value = false
  emit("onApply", {
    column: selectedSortColumnIdInternal.value,
    isAsc: isSortAscInternal.value
  })
}

const onCancelClick = () => {
  computedModel.value = false
}

</script>

<style scoped>

.message-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.btn-wrapper {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.sort-arrow-as-hint {
  display: none;
  color: var(--harmony-light-3);
}

.header-cell-content:hover .sort-arrow-as-hint {
  display: inline-block;
}

</style>