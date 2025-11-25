<template>
  <div id="root">
    <table>
      <!-- HEADER -->
      <tr>
        <th v-for="(column, index) in columns" :key="index">
          <div class="header-cell" @click="onSortSelectionChangeClick(column.id)">
            <slot :name="'header:' + column.id">
              <p :style="isSortable ? 'cursor: pointer' : ''">{{column.caption}}</p>
            </slot>
            <template v-if="isSortable">
              <template v-if="column.id === selectedSortColumnId">
                <span v-if="isSortAsc">&#8593;</span>
                <span v-else>&darr;</span>
              </template>
              <template v-else>
                <span class="sort-arrow-as-hint">&#8593;</span>
              </template>
            </template>
          </div>
        </th>
      </tr>
      <!-- DATA -->
      <tr v-for="(row, rowIndex) in rows" :key="rowIndex" :style="isOpenable ? 'cursor: pointer' : ''" @click="onRowClick(row)">
        <td v-for="(column, columnIndex) in columns" :key="columnIndex">
          <div class="cell-content">
            <slot :name="'column:' + column.id" :row="row">
              <slot name="column" :cell="cellResolver(row, column)" :row="row">
                <p class="cell-text">{{cellResolver(row, column)}}</p>
              </slot>
            </slot>
          </div>
        </td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts" setup>

import {PropType} from "vue";
import {HarmonyTableColumn} from "~/utils/HarmonyTypes";

const props = defineProps({
  columns: {
    type: Array as PropType<HarmonyTableColumn[]>,
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
  rows: {
    type: Array as PropType<Object>,
    required: true
  },
  cellResolver: {
    type: Function,
    required: true
  },
  isOpenable: {
    type: Boolean,
    required: true
  }
})

const emit = defineEmits<{
  (e: 'onSortingOptionsChanged', options: any): void,
  (e: 'onRowClick', row: any): void
}>()

const onSortSelectionChangeClick = (columnId: string) => {
  emit("onSortingOptionsChanged", columnId)
}

const onRowClick = (row: any) => {
  if(props.isOpenable) {
    emit('onRowClick', row)
  }
}

</script>

<style scoped>

.header-cell {
  display: flex;
  padding: 10px;
}

.sort-arrow-as-hint {
  display: none;
  color: var(--harmony-light-3);
}

.header-cell:hover .sort-arrow-as-hint {
  display: inline-block;
}

table {
  width: 100%;
  border-collapse: collapse;
}


table, th, td {
  border: 0;
}

th {
  width: 50px;
  background-color: var(--harmony-light-2);
  position: sticky;
  top: 0;
  z-index: 2;
}

.cell-content {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  background-color: var(--harmony-light-1);
  color: var(--harmony-black);
  margin-top: 5px;
  margin-bottom: 5px;
  padding: 10px;
  height: 60px;
}

td:first-child .cell-content {
  border-top-left-radius: 15px;
  border-bottom-left-radius: 15px;
}

td:last-child .cell-content {
  border-top-right-radius: 15px;
  border-bottom-right-radius: 15px;
}

.cell-text {
  overflow: hidden;
  max-width: 100%;
  text-overflow: ellipsis;
  width: fit-content;
  white-space: nowrap;
}

</style>