<template>
  <client-only>
    <div id="container">
      <div id="header">
        <div id="header-items">
          <div id="custom-action-area">
            <slot name="custom-action-header" />
          </div>
          <div>
            <HarmonyIconSwitchButton :options="tableFormatOptions" v-model="currentTableRepresentations"/>
          </div>
        </div>
        <slot name="sub-header" />
      </div>
      <div id="scroll-container" class="hidden-scrollbar">
        <template v-if="isInLoadingMode">
          <div id="loading-area">
            <LoadingSpinner />
          </div>
        </template>
        <template v-else-if="currentTableRepresentations === TableRepresentation.TABLE">
          <HarmonyTableTableRepresentation
              :columns="internalColumns"
              :is-sortable="isSortable"
              :selected-sort-column-id="selectedSortColumnId"
              :is-sort-asc="isSortAsc"
              @onSortingOptionsChanged="onSortingOptionsChanged"
              :rows="rows"
              :cell-resolver="getValueByRowAndColumn"
              :is-openable="isOpenable"
              @onRowClick="onEntryClicked"
          >

            <template v-for="name in customHeaders" v-slot:[name]>
              <slot :name="name" />
            </template>

            <template v-for="name in customColumns" v-slot:[name]="{ row }" >
              <slot :name="name" :row="row"/>
            </template>

            <!-- This applies generic for all cells if not overriden by custom column slot -->
            <template v-slot:column="{cell, row}" >
              <slot name="column" :cell="cell" :row="row"/>
            </template>

          </HarmonyTableTableRepresentation>
        </template>
        <template v-else-if="currentTableRepresentations === TableRepresentation.CARD">
          <HarmonyTableCardRepresentation
              :columns="columns"
              :rows="rows"
              :cell-resolver="getValueByRowAndColumn"
              :is-sortable="isSortable"
              :selected-sort-column-id="selectedSortColumnId"
              :is-sort-asc="isSortAsc"
              :is-openable="isOpenable"
              @onSortingOptionsChangedForCard="onSortingOptionsChangedForCard"
              @onRowClick="onEntryClicked"
          >

            <template v-for="name in customHeaders" v-slot:[name]>
              <slot :name="name" />
            </template>

            <template v-for="name in customColumns" v-slot:[name]="{ row }" >
              <slot :name="name" :row="row"/>
            </template>

            <!-- This applies generic for all cells if not overriden by custom column slot -->
            <template v-slot:column="{cell, row}" >
              <slot name="column" :cell="cell" :row="row"/>
            </template>

          </HarmonyTableCardRepresentation>
        </template>
      </div>
    </div>
  </client-only>
</template>

<script lang="ts" setup>

import {computed, onMounted, PropType, ref} from "vue";
import HarmonyTableTableRepresentation from "@core/components/base/table/HarmonyTableTableRepresentation.vue";
import HarmonyTableCardRepresentation from "@core/components/base/table/HarmonyTableCardRepresentation.vue";
import HarmonyIconSwitchButton from "@core/components/base/HarmonySwitchButton.vue"
import JsonUtils from "~/utils/JsonUtils";
import {nextTick, useSlots} from "vue";
import LoadingSpinner from "@core/components/utils/LoadingSpinner.vue";
import I18N from "~/utils/I18N";
import {HarmonyTableColumn, SwitchButtonOption} from "~/utils/HarmonyTypes";
import useLanguageUtils from "~/composables/useLanguageUtils";

const i18n = I18N.of("HarmonyTable")

enum TableRepresentation {
  TABLE = "TABLE",
  CARD = "CARD"
}

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  isSortable: {
    type: Boolean,
    required: false
  },
  rows: {
    type: Array as PropType<Object>,
    required: true
  },
  onOpenEntry: {
    type: Function,
    required: false
  },
  proposedTableFormat: {
    type: String,
    required: false,
    default: TableRepresentation.TABLE
  },
  isInLoadingMode: {
    type: Boolean,
    required: false,
    default: false
  },
  initialSortByColumnId: {
    type: String,
    required: false
  },
  isInitialSortAsc: {
    type: Boolean,
    required: false,
    default: true
  }
})

const emit = defineEmits<{
  (e: 'onSortingOptionsChanged', options: any): void,
  (e: 'onBottomScrolled', delta: number): void,
}>()

const languageUtils = useLanguageUtils()

const initialProposedTableRepresentation:TableRepresentation = props.proposedTableFormat == TableRepresentation.TABLE ? TableRepresentation.TABLE : TableRepresentation.CARD
const currentTableRepresentations = ref<TableRepresentation>(initialProposedTableRepresentation)

const tableFormatOptions:SwitchButtonOption[] = [
  {
    id: TableRepresentation.TABLE,
    iCssClass: "bx bx-table"
  },
  {
    id: TableRepresentation.CARD,
    iCssClass: "bx bx-rectangle"
  }
]

const selectedSortColumnId = ref<string | undefined>(props.initialSortByColumnId)
const isSortAsc = ref(props.isInitialSortAsc)

const isOpenable = ref(!!props.onOpenEntry)

const fixedLeftColumns = computed(() => [...props.columns.filter(c => c.fixedLeft)])
const nonFixedLeftColumns = computed(() => [...props.columns.filter(c => !c.fixedLeft)])

const internalColumns= computed<HarmonyTableColumn[]>(() => [...fixedLeftColumns.value, ...nonFixedLeftColumns.value])

const emitSortingOptions = () => {
  const newSortOptions = {
    column: selectedSortColumnId.value,
    isAsc: isSortAsc.value
  }

  emit('onSortingOptionsChanged', newSortOptions)
}

const onSortingOptionsChanged = (columnId:string) => {
  if(!props.isSortable)
    return

  if(selectedSortColumnId.value === columnId) {
    if(isSortAsc.value) {
      isSortAsc.value = false
    } else {
      isSortAsc.value = true
      selectedSortColumnId.value = undefined
    }
  } else {
    selectedSortColumnId.value = columnId
    isSortAsc.value = true
  }

  emitSortingOptions()
}

const onSortingOptionsChangedForCard = (columnId:string, isAsc: boolean) => {
  if(!props.isSortable)
    return

  if(columnId == undefined) {
    selectedSortColumnId.value = undefined
  } else {
    selectedSortColumnId.value = columnId
    isSortAsc.value = isAsc
  }

  emitSortingOptions()
}

const getValueByRowAndColumn = (row: Object, column: HarmonyTableColumn):any => {
  const valueById = column.path === "" ? row : JsonUtils.getFieldValueByPath(row, column.path!)
  if(valueById != undefined) {
    if(typeof valueById == "boolean") { // this waring is not correct
      return valueById ? i18n.translate("yes").build() : i18n.translate("no").build()
    } else if(!!column.isDate){
      return languageUtils.formatLanguageByLocalState(valueById)
    } else {
      return valueById
    }
  } else {
    return "-"
  }
}

const onEntryClicked = (entry: any) =>{
  if(props.onOpenEntry) {
    props.onOpenEntry(entry)
  }
}

const onBottomScrolled = (delta: number) => {
  emit("onBottomScrolled", delta)
}

const initScrollListener = () => {
  const edgeLimit = 20;

  const scrollContainerElement = document.getElementById("scroll-container")!

  const heightForContainer = scrollContainerElement.offsetHeight
  let isInTopArea = true
  scrollContainerElement.addEventListener("scroll", () => {
    const maxScroll = scrollContainerElement.scrollHeight - heightForContainer
    const delta = maxScroll - scrollContainerElement.scrollTop
    if(delta <= edgeLimit && isInTopArea) {
      isInTopArea = false
      onBottomScrolled(delta)
    } else if(delta > edgeLimit && !isInTopArea) {
      isInTopArea = true;
    }
  })
}

onMounted(() => {
  nextTick(() => initScrollListener());
})

const slots = useSlots()
const customHeaders = Object.keys(slots).filter(c => c.startsWith("header:"))
const customColumns = Object.keys(slots).filter(c => c.startsWith("column:"))

</script>

<style scoped>

#container {
  height: 100%;
  overflow: auto;
  display: flex;
  flex-flow: column;
}

#header {
  flex: 0 1 auto;
  padding-bottom: 1em;
}

#header-items {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

#scroll-container {
  flex: 1 1 auto;
  overflow: auto;
}

/* Hide scrollbar for Chrome, Safari and Opera */
.hidden-scrollbar::-webkit-scrollbar {
  display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.hidden-scrollbar{
  -ms-overflow-style: none;  /* IE and Edge */
  scrollbar-width: none;  /* Firefox */
}

</style>