<template>
  <HarmonyModal v-model="isSortModalOpen" :is-minimal="true">
    <h3>{{i18n.translate('Sorting').build()}}</h3>
    <template v-for="(column, index) in columns" :key="index">
      <HarmonyPanel>
        <div class="sort-modal-row">
          <div class="sort-modal-row-item">
            <p>{{column.caption}}</p>
          </div>
          <div class="sort-modal-row-item" style="display: flex">
            <i class='bx bxs-up-arrow-circle' :class="activeSort?.columnId === column.id && activeSort.isSortAsc ? 'sort-modal-icon-active' : 'sort-modal-icon'" @click="onSortArrowClick(column.id, true)"></i>
            <i class='bx bxs-down-arrow-circle' :class="activeSort?.columnId === column.id && !activeSort.isSortAsc ? 'sort-modal-icon-active' : 'sort-modal-icon'" @click="onSortArrowClick(column.id, false)"></i>
          </div>
        </div>
      </HarmonyPanel>
    </template>
  </HarmonyModal>
  <div id="root">
    <div id="header-section">
      <i v-if="isSortable" class='bx bx-sort-alt-2 card-sorting-title' @click="isSortModalOpen = true"/>
      <template v-for="(column, index) in fixedLeftColumns" :key="index">
        <div style="margin-left: 1.75em">
          <slot :name="'header:' + column.id"></slot>
        </div>
      </template>
    </div>
    <div v-for="(row, index) in rows" :key="index">
      <HarmonyPanel class="panel" :style="isOpenable ? 'cursor: pointer' : ''" @click="onItemPanelClick(row)">
        <div v-for="(column, index) in fixedLeftColumns" :key="index" class="item">
          <slot :name="'column:' + column.id" :row="row">
            <slot name="column" :cell="cellResolver(row, column)" :row="row">
              <p><b>{{column.caption}}</b></p>
              <p>{{cellResolver(row, column)}}</p>
            </slot>
          </slot>
        </div>
        <div class="panel-items">
          <div v-for="(column, index) in nonFixedLeftColumns" :key="index" class="item">
            <slot :name="'column:' + column.id" :row="row">
              <slot name="column" :cell="cellResolver(row, column)" :row="row">
                <p><b>{{column.caption}}</b></p>
                <p>{{cellResolver(row, column)}}</p>
              </slot>
            </slot>
          </div>
        </div>
      </HarmonyPanel>
    </div>
  </div>
</template>

<script lang="ts" setup>

import {PropType} from "vue";
import HarmonyPanel from "@core/components/base/HarmonyPanel.vue";
import HarmonyModal from "@core/components/base/HarmonyModal.vue";
import I18N from "@core/utils/I18N";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const i18n = I18N.of("HarmonyTableCardRepresentation")

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
    required: true
  },
  rows: {
    type: Array as PropType<Object>,
    required: true
  },
  cellResolver: {
    type: Function,
    required: true
  },
  isSortable: {
    type: Boolean,
    required: true
  },
  selectedSortColumnId: {
    type: String,
    required: false
  },
  isSortAsc: {
    type: Boolean,
    required: false
  },
  isOpenable: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'onSortingOptionsChangedForCard', columnId?: string, isSortAsc?: boolean): void,
  (e: 'onRowClick', row: any): void
}>()

const fixedLeftColumns = computed(() => [...props.columns.filter(c => c.fixedLeft)])
const nonFixedLeftColumns = computed(() => [...props.columns.filter(c => !c.fixedLeft)])

const isSortModalOpen = ref(false)

const activeSort = computed(() => {
  if(!props.isSortable || !props.selectedSortColumnId) {
    return undefined;
  }
  return {
    columnId: props.selectedSortColumnId,
    isSortAsc: props.isSortAsc
  }
})

const onSortArrowClick = (columnId: string, isSortAsc: boolean) => {
  if(activeSort.value?.columnId == columnId && activeSort.value?.isSortAsc == isSortAsc) {
    emit("onSortingOptionsChangedForCard", undefined, undefined);
  } else {
    emit("onSortingOptionsChangedForCard", columnId, isSortAsc)
  }

  isSortModalOpen.value = false
}

const onItemPanelClick = (row: any) => {
  if(!props.isOpenable)
    return

  emit('onRowClick', row)
}


</script>

<style scoped>

.panel {
  margin: 1em 0 1em 0;
  display: flex;
}

.card-sorting-title {
  color: var(--harmony-light-3);
  cursor: pointer;
}

.panel-items {
  display: flex;
  flex-wrap: wrap;
}

.item {
  margin: 1em;
}

#header-section {
  display: flex;
  padding-left: 8px;
}

.sort-modal-row {
  display: flex;
  width: 100%;
}

.sort-modal-row-item {
  width: 50%;
}

.sort-modal-icon {
  cursor: pointer;
  border-radius: 20px;
  padding: 5px;
  background-color: var(--harmony-light-1);
  color: var(--harmony-light-3);
}


.sort-modal-icon-active {
  cursor: pointer;
  border-radius: 20px;
  padding: 5px;
  background-color: var(--harmony-light-3);
  color: var(--harmony-light-1);
}

.sort-modal-icon:hover {
  background-color: var(--harmony-light-3);
  color: var(--harmony-light-1);
}

</style>