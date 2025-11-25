<template>
  <HarmonyModal v-model="isModelVisible">
    <HarmonyContentSwitcher :steps="switcherSteps" v-model="internalActiveStep">
      <template #default>
        <div class="select-modal-container">
          <div class="header-row">
            <div class="search-area">
              <harmony-text-field style="height: 2em; min-width: 5em" v-model="searchText" caption="" :placeholder="i18n.translate('Search...').build()"/>
            </div>
            <div class="button-group">
              <HarmonyButton :caption="i18n.translate('Cancel').build()" :type="HarmonyButtonType.SECONDARY" style="margin-right: 10px" @click="onCancelClick"/>
              <HarmonyButton :caption="i18n.translate('Apply').build()" @click="onApplyClick"/>
            </div>
          </div>
          <HarmonySelectTable :columns="columns" :rows="internalRows" :card-representation="false" v-model="selectTableVModel" :single-selection="singleSelection">
            <template #custom-action-header>
              <i v-if="addButtonVisible" class='bx bx-plus-circle bx-md add-new-icon' @click="onAddNewClick"></i>
            </template>
          </HarmonySelectTable>
        </div>
      </template>
      <template v-for="name in switcherTemplates" v-slot:[name]>
        <slot :name="name"></slot>
      </template>
    </HarmonyContentSwitcher>
  </HarmonyModal>
  <HarmonyTextField
      v-model="computedTextFieldValue"
      :caption="caption"
      inner-icon="bx-dots-horizontal-rounded"
      :is-readonly="true"
      @click="onTextFieldClick"
      :error-messages="errorMessages"
  />
</template>

<script lang="ts" setup>
import {PropType, ref, watch} from "vue";
import HarmonyTextField from "@core/components/base/HarmonyTextField.vue";
import HarmonySelectTable from "@core/components/base/HarmonySelectTable.vue";
import {ComputedUtils} from "~/utils/ComputedUtils";
import JsonUtils from "~/utils/JsonUtils";
import I18N from "@core/utils/I18N";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import HarmonyButton from "@core/components/base/HarmonyButton.vue";
import {HarmonyButtonType, HarmonyContentSwitcherType, HarmonyTableColumn} from "~/utils/HarmonyTypes";
import HarmonyContentSwitcher from "~/components/utils/HarmonyContentSwitcher.vue";

const props = defineProps({
  caption: {
    type: String,
    required: true
  },
  modelValue: {
    type: Array,
    required: true
  },
  rows: {
    type: Array as PropType<Object[]>,
    required: true
  },
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  singleSelection: {
    type: Boolean,
    required: false,
    default: false
  },
  errorMessages: {
    type: Array,
    required: false
  },
  showPreview: {
    type: Boolean,
    required: false,
    default: false
  },
  previewItemPath: {
    type: String,
    required: false,
    default: ""
  },
  isReadonly: {
    type: Boolean,
    required: false,
    default: false
  },
  addButtonVisible: {
    type: Boolean,
    required: false,
    default: false
  },
  activeStep: {
    type: String,
    required: false,
    default: "default"
  },
  switcherSteps: {
    type: Array as PropType<HarmonyContentSwitcherType[]>,
    required: false,
    default: [{id: "default"}]
  }
})

const i18n = I18N.of("HarmonySelectField")

const isModelVisible = ref<boolean>(false)

const emit = defineEmits<{
  (e: 'onAddNewClick'): void,
  (e: 'update:modelValue'): void
}>()

const onTextFieldClick = () => {
  if(!props.isReadonly)
    isModelVisible.value = true
}

const computedModel = ComputedUtils.createDelegatedComputedValue(props, getCurrentInstance()!)

const computedTextFieldValue = computed(() => {
  const appliedElements: any[] = computedModel.value
  const countOfSelectedItems: number = appliedElements.length
  if(countOfSelectedItems > 0) {
    if(props.showPreview) {
      return appliedElements.map(e => JsonUtils.getFieldValueByPath(e, props.previewItemPath)).join(", ")
    } else if(countOfSelectedItems == 1) {
      return i18n.translate("Single element selected").build()
    } else {
      return i18n.translate("{count} elements selected").add("count", countOfSelectedItems).build()
    }
  } else  {
    return i18n.translate("Nothing selected").build()
  }
})

const selectTableVModel = ref([...props.modelValue])

const reInitTableModel = () => selectTableVModel.value = [...props.modelValue]
watch(isModelVisible, reInitTableModel)

const onCancelClick = () => isModelVisible.value = false
const onApplyClick = () => {
  computedModel.value = [...selectTableVModel.value]
  isModelVisible.value = false
}

const searchText = ref("")

const rowMatchesSearch = (row: any, searchText: string):boolean => {
  const jsonText = JSON.stringify(row).toLowerCase()
  return jsonText.includes(searchText.toLowerCase())
}

const internalRows = computed(() => {
  return props.rows?.filter(r => rowMatchesSearch(r, searchText.value))
})

const internalActiveStep = computed(() => props.activeStep)
const onAddNewClick = () => emit("onAddNewClick")

const switcherTemplates = props.switcherSteps.map(s => s.id).filter(v => v != "default")

const addItemToSelectList = (item:any) => selectTableVModel.value.push(item)

defineExpose({
  addItemToSelectList
})

</script>

<style scoped>
.search-area {
  width: 45vw;
  min-width: 20em;
  margin-right: 5%;
  margin-left: 1em;
  margin-bottom: 1em;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap-reverse;
}

.add-new-icon {
  color: var(--harmony-light-4);
  margin: 0 0.5em;
  cursor: pointer;
}

.button-group {
  margin-left: 1em;
  margin-top: 20px;
  display: flex;
  align-items: center;
  justify-content: right;
  margin-bottom: 10px;
}

.select-modal-container {
  display: flex;
  flex-flow: column;
  height: 100%;
}

</style>